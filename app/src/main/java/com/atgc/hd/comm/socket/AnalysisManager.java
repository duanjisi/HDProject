package com.atgc.hd.comm.socket;

import com.alibaba.fastjson.JSON;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.net.response.base.BaseResponse;
import com.atgc.hd.comm.net.response.base.Response;
import com.atgc.hd.comm.utils.CRCUtil;
import com.atgc.hd.comm.utils.DigitalUtils;
import com.atgc.hd.comm.utils.StringUtils;
import com.orhanobut.logger.Logger;
import com.xuhao.android.libsocket.sdk.bean.OriginalData;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>描述：报文分析
 * <p>作者：liangguokui 2018/2/10
 */
public class AnalysisManager {
    private Map<String, Class<?>> mapResponseClass = new HashMap<>();
    private Map<String, OnActionListener> mapActionListeners = new HashMap<>();

    public void setResponseClass(String cmd, Class<?> responseClass) {
        if (mapResponseClass.containsKey(cmd)) {
        } else {
            mapResponseClass.put(cmd, responseClass);
        }
    }

    public void onReceiveResponse(OriginalData data) {
        String header = DigitalUtils.byteArrayToHexString(data.getHeadBytes());
        String body = new String(data.getBodyBytes(), Charset.forName("utf-8"));

        Logger.e("响应报文头：\n" + header + "\n响应报文体：\n" + body);

        boolean verifyOK = checkCrc(data);
        if (verifyOK) {
            onReceiveResponse(body);
        } else {
            Logger.e("数据包CRC验证失败" + "\nheader：" + header + "\nbody：" + body);
        }

    }

    public void onReceiveResponse(String body) {
        if (StringUtils.isEmpty(body)) {
            return;
        }

        Response response = JSON.parseObject(body, Response.class);

        OnActionListener actionListener = null;

        // 当注册有该Command的listener时，才会对业务数据进行解析并回调
        if (mapActionListeners.containsKey(response.Command)) {
            actionListener = mapActionListeners.get(response.Command);
        }

        // 因为不用回调，所以数据也不需解析了
        if (actionListener == null) {
            return;
        }

        // 设备注册口需做特殊处理，网关不支持返回requestTag
        if (DeviceCmd.REGISTER.equals(response.Command)) {
            if (response.Result == 0) {
                actionListener.onResponseSuccess(response.Command, "", response);
            } else {
                actionListener.onResponseFaile(response.Command, "", "" + response.ErrorCode, response.ErrorMessage);
            }
        }

        if (response.Data == null) {
            return;
        }

        Class<?> respClass = mapResponseClass.get(response.Command);
        response.parseData(respClass);

        List<BaseResponse> baseResponses = response.dataArray;
        BaseResponse baseResponse = baseResponses.isEmpty() ? null : baseResponses.get(0);
        if (baseResponse == null) {
            actionListener.onSendFail(
                    response.Command,
                    "",
                    "" + response.ErrorCode,
                    response.ErrorMessage);
            actionListener.onResponseFaile(
                    response.Command,
                    "",
                    "999",
                    "暂无数据");
            return;
        }

        // S 端返回给C 端的回执
        if (baseResponse.isReceipt()) {
            if (response.Result == 0) {
                actionListener.onSendSucess(
                        response.Command,
                        baseResponse.serialNum);
            } else {
                actionListener.onSendFail(
                        response.Command,
                        baseResponse.serialNum,
                        "" + response.ErrorCode,
                        response.ErrorMessage);
            }
        }
        // S 端下发到C 端的业务数据
        else {
            if ("0".equals(baseResponse.serverResult)) {
                actionListener.onResponseSuccess(
                        response.Command,
                        baseResponse.serialNum,
                        response);
            } else {
                actionListener.onResponseFaile(
                        response.Command,
                        baseResponse.serialNum,
                        baseResponse.serverResult,
                        baseResponse.errMsg);
            }
        }
    }

    /**
     * 适用于：没有请求只有数据接收的情况注册
     * <p>注意及时调用{@link #unRegistertOnActionListener(String)}注销
     *
     * @param cmd
     * @param responseClass
     * @param listener
     */
    public void registertOnActionListener(String cmd, Class<?> responseClass, OnActionListener listener) {
        setResponseClass(cmd, responseClass);
        registertOnActionListener(cmd, listener);
    }

    /**
     * 适用于：有请求有数据接收的情况注册
     * <p>注意及时调用{@link #unRegistertOnActionListener(String)}注销
     *
     * @param cmd
     * @param listener
     */
    public void registertOnActionListener(String cmd, OnActionListener listener) {
        if (mapActionListeners.containsKey(cmd)) {
        } else {
            mapActionListeners.put(cmd, listener);
        }
    }

    public void unRegistertOnActionListener(String cmd) {
        mapActionListeners.remove(cmd);
        mapResponseClass.remove(cmd);
    }

    private boolean checkCrc(OriginalData data) {
        byte[] crc = new byte[2];
        System.arraycopy(data.getHeadBytes(), 55, crc, 0, 2);
        return CRCUtil.isPassCRC(data.getBodyBytes(), crc);
    }
}

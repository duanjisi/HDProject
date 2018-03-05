package com.atgc.hd.comm.socket;

import android.os.Bundle;

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
        Class<?> respClass = mapResponseClass.get(response.Command);
        response.parseData(respClass);

        OnActionListener actionListener = getOnActionListener(response);
        // 因为不用回调，所以数据也不需解析了
        if (actionListener == null) {
            return;
        }

        Bundle bundle = null;
        if (response.dataArray != null && !response.dataArray.isEmpty()) {
            BaseResponse baseResponse = (BaseResponse) response.dataArray.get(0);
            bundle = mapRequestBundle.get(baseResponse.serialNum);
        }

        // 设备注册口需做特殊处理，网关不支持返回requestTag
        if (DeviceCmd.REGISTER.equals(response.Command)) {
            if (response.Result == 0) {
                actionListener.onResponseSuccess(response.Command, "", response, bundle);
            } else {
                actionListener.onResponseFaile(response.Command, "", "" + response.ErrorCode, response.ErrorMessage, bundle);
            }
        }

        if (response.Data == null) {
            return;
        }

        if (response.dataArray.isEmpty()) {
            actionListener.onSendFail(
                    response.Command,
                    "",
                    "" + response.ErrorCode,
                    response.ErrorMessage,
                    bundle);
            actionListener.onResponseFaile(
                    response.Command,
                    "",
                    "999",
                    "暂无数据",
                    bundle);
            return;
        }

        String serialNum;
        String serverResult;
        String errMsg;
        boolean isReceipt;
        if (respClass == BaseResponse.class || respClass.getSuperclass() == BaseResponse.class) {
            List<BaseResponse> baseResponses = response.dataArray;
            BaseResponse baseResponse = baseResponses.isEmpty() ? null : baseResponses.get(0);

            serialNum = baseResponse.serialNum;
            serverResult = baseResponse.serverResult;
            errMsg = baseResponse.errMsg;
            isReceipt = baseResponse.isReceipt();
        } else {
            serialNum = "";
            serverResult = response.Result == 0 ? "0" : String.valueOf(response.ErrorCode);
            errMsg = response.ErrorMessage;
            isReceipt = false;
        }

        // S 端返回给C 端的回执
        if (isReceipt) {
            if (response.Result == 0) {
                actionListener.onSendSucess(
                        response.Command,
                        serialNum,
                        bundle);
            } else {
                actionListener.onSendFail(
                        response.Command,
                        serialNum,
                        "" + response.ErrorCode,
                        response.ErrorMessage,
                        bundle);
            }
        }
        // S 端下发到C 端的业务数据
        else {
            if ("0".equals(serverResult)) {
                actionListener.onResponseSuccess(
                        response.Command,
                        serialNum,
                        response,
                        bundle);
            } else {
                actionListener.onResponseFaile(
                        response.Command,
                        serialNum,
                        serverResult,
                        errMsg,
                        bundle);
            }
        }
    }

    private OnActionListener getOnActionListener(Response response) {
        String key = getPoolListenerKey(response);

        // 当注册有该Command的listener时，才会对业务数据进行解析并回调
        if (mapPoolListener.containsKey(key)) {
            return mapPoolListener.get(key);
        } else if (mapPoolListenersNoRequestTag.containsKey(response.Command)) {
            return mapPoolListenersNoRequestTag.get(response.Command);
        } else {
            return null;
        }
    }

    private String getPoolListenerKey(Response response) {
        String key;
        if (response.dataArray == null) {
            key = response.Command;
        } else {
            if (response.dataArray.isEmpty()) {
                key = "";
            } else {
                Object object = response.dataArray.get(0);
                if (object instanceof BaseResponse) {
                    BaseResponse baseResponse = (BaseResponse) object;
                    key = baseResponse.serialNum;
                } else {
                    key = response.Command;
                }
            }
        }
        return key;
    }


    private Map<String, Bundle> mapRequestBundle = new HashMap<>();
    private Map<String, Class<?>> mapResponseClass = new HashMap<>();

    private Map<String, Map<String, OnActionListener>> mapGroupListener = new HashMap<>();
    private Map<String, OnActionListener> mapPoolListener = new HashMap<>();
    private Map<String, OnActionListener> mapPoolListenersNoRequestTag = new HashMap<>();

    private void setResponseClass(String cmd, Class<?> responseClass) {
        if (mapResponseClass.containsKey(cmd)) {
        } else {
            mapResponseClass.put(cmd, responseClass);
        }
    }

    /**
     * 适用请求的响应报文有
     *
     * @param groupTag
     * @param cmd
     * @param requestTag
     * @param onResponseNoRequestTag 响应报文是否有requestTag返回
     */
    public void preAnalysisResponse(String groupTag,
                                    String cmd,
                                    String requestTag,
                                    Bundle bundle,
                                    boolean onResponseNoRequestTag) {
        mapRequestBundle.put(requestTag, bundle);

        Map<String, OnActionListener> mapListener = mapGroupListener.get(groupTag);

        if (mapListener == null) {
            return;
        }

        OnActionListener listener = mapListener.get(cmd);
        if (onResponseNoRequestTag) {
            mapPoolListenersNoRequestTag.put(cmd, listener);
        } else {
            mapPoolListener.put(requestTag, listener);
        }
    }

    /**
     * @param groupTag
     * @param cmd
     */
    public void preAnalysisResponseNoRequestTag(String groupTag, String cmd, Bundle bundle) {
        mapRequestBundle.put(cmd, bundle);
        Map<String, OnActionListener> mapListener = mapGroupListener.get(groupTag);
        if (mapListener == null) {
        } else {
            OnActionListener listener = mapListener.get(cmd);
            mapPoolListenersNoRequestTag.put(cmd, listener);
        }
    }

    /**
     * groupTag相同的情况下，一个cmd只能注册一次，否则回调会出现问题
     * <p>注意及时调用{@link #unRegistertOnActionListener(String)}注销
     *
     * @param groupTag      同一个类里面，多次注册传相同的groupTag，
     *                      在注销接口传groupTag，可将同一groupTag的listener都移除
     * @param cmd           响应接口命令字
     * @param responseClass 响应报文解析类
     * @param listener      回调监听器
     */
    public void registertOnActionListener(String groupTag,
                                          String cmd,
                                          Class<?> responseClass,
                                          OnActionListener listener) {
        setResponseClass(cmd, responseClass);
        Map<String, OnActionListener> mapListener = mapGroupListener.get(groupTag);

        if (mapListener == null) {
            mapListener = new HashMap<>();
            mapGroupListener.put(groupTag, mapListener);
        } else if (mapListener.containsKey(cmd)) {
            throw new IllegalArgumentException("暂不支持同一【groupTag：" + groupTag + "】注册多次【cmd：" + cmd + "】");
        }

        mapListener.put(cmd, listener);
    }

    public void unRegistertOnActionListener(String groupTag) {
        mapGroupListener.remove(groupTag);
    }

    public void unRegistertOnActionListener(String groupTag, String cmd) {
        mapResponseClass.remove(cmd);
        Map<String, OnActionListener> mapListeners = mapGroupListener.get(groupTag);
        if (mapListeners == null) {
        } else {
            mapListeners.remove(cmd);
        }
    }

    private boolean checkCrc(OriginalData data) {
        byte[] crc = new byte[2];
        System.arraycopy(data.getHeadBytes(), 55, crc, 0, 2);
        return CRCUtil.isPassCRC(data.getBodyBytes(), crc);
    }
}

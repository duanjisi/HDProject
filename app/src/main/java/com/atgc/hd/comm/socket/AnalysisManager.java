package com.atgc.hd.comm.socket;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.hdsocket.net.response.BaseResponse;
import com.hdsocket.net.response.Response;
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
        String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
        Log.e("AnalysisManager", "onReceiveResponse --- " + str);

        Response response = JSON.parseObject(str, Response.class);

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
        if ("".equals(response.Command)) {
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
        String serialNum = baseResponse == null ? "" : baseResponse.serialNum;

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
}
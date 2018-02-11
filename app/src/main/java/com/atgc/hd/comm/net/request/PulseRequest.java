package com.atgc.hd.comm.net.request;

import com.hdsocket.net.request.BaseRequest;

/**
 * <p>描述：心跳包数据
 * <p>作者：liangguokui 2018/2/7
 */
public class PulseRequest extends BaseRequest {
    @Override
    public String getRequestCommand() {
        return "COM_HEARTBEAT";
    }

    @Override
    public String getResponseCommand() {
        return null;
    }

    @Override
    public Class<?> getResponseClass() {
        return null;
    }
}

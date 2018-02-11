package com.atgc.hd.comm.net.request;

import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.net.request.base.BaseRequest;

/**
 * <p>描述：心跳包数据
 * <p>作者：liangguokui 2018/2/7
 */
public class PulseRequest extends BaseRequest {
    @Override
    public String getRequestCommand() {
        return DeviceCmd.HEART_BEAT;
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

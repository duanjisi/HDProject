package com.atgc.hd.comm.net.request;

import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.net.request.base.BaseRequest;
import com.atgc.hd.comm.net.response.base.BaseResponse;

/**
 * <p>描述：设备通用反馈上报
 * <p>作者：liangguokui 2018/3/12
 */
public class FeedBackRequest extends BaseRequest {
    public String messageId;
    public String type;
    public String serverResult;
    public String errMsg;
    public String deviceId = DeviceParams.getInstance().getDeviceId();

    @Override
    public String getRequestCommand() {
        return DeviceCmd.PAT_SEND_MESSAGE;
    }

    @Override
    public String getResponseCommand() {
        return DeviceCmd.PAT_SEND_MESSAGE;
    }

    @Override
    public Class<?> getResponseClass() {
        return BaseResponse.class;
    }
}

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
    public String messageID;
    public String type;
    public String serverResult;
    public String errMsg;
    public String deviceID = DeviceParams.getInstance().getDeviceId();

    @Override
    public String getRequestCommand() {
        return DeviceCmd.PAT_DEV_EXE_FEEDBACK;
    }

    @Override
    public String getResponseCommand() {
        return DeviceCmd.PAT_DEV_EXE_FEEDBACK;
    }

    @Override
    public Class<?> getResponseClass() {
        return BaseResponse.class;
    }
}

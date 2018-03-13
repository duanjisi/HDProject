package com.atgc.hd.comm.net.request;

import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.net.request.base.BaseRequest;
import com.atgc.hd.comm.net.response.base.BaseResponse;
import com.atgc.hd.comm.utils.DateUtil;

/**
 * <p>描述：派遣人员完成任务
 * <p>作者：liangguokui 2018/3/13
 */
public class DispatchFinishRequest extends BaseRequest {
    public String messageID;
    public String deviceID;
    public String uploadTime;

    public DispatchFinishRequest() {
        deviceID = DeviceParams.getInstance().getDeviceId();
        uploadTime = DateUtil.currentTime();
    }

    @Override
    public String getRequestCommand() {
        return DeviceCmd.PAT_UPLOAD_SENDPERSON_FINISH;
    }

    @Override
    public String getResponseCommand() {
        return DeviceCmd.PAT_UPLOAD_SENDPERSON_FINISH;
    }

    @Override
    public Class<?> getResponseClass() {
        return BaseResponse.class;
    }
}

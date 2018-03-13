package com.atgc.hd.comm.net.request;

import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.net.request.base.BaseRequest;
import com.atgc.hd.comm.net.response.TaskListResponse;

/**
 * <p>描述：请求巡更任务
 * <p>作者：liangguokui 2018/1/23
 */
public class GetTaskRequest extends BaseRequest {
    public String deviceID;

    public GetTaskRequest() {
        if (Constants.isDemo) {
            serialNum = "1354jhjy546hjvj";
        }
    }

    @Override
    public String getRequestCommand() {
        return DeviceCmd.PAT_GET_TASK_REQUEST;
    }

    @Override
    public String getResponseCommand() {
        return DeviceCmd.PAT_SEND_TASK;
    }

    @Override
    public Class<?> getResponseClass() {
        return TaskListResponse.class;
    }
}

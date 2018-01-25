package com.atgc.hd.comm.net.request;

import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.net.BaseDataRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>描述：请求巡更任务
 * <p>作者：liangguokui 2018/1/23
 */
public class GetTaskRequest extends BaseDataRequest<String> {
    private String deviceID;

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        map.put("deviceID", deviceID);
        return map;
    }

    @Override
    protected String getCommand() {
        return DeviceCmd.PAT_GET_TASK_REQUEST;

    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }
}

package com.atgc.hd.comm.net.request;

import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.net.BaseDataRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>描述：8.3.33	上报巡查任务状态
 * <p>作者：liangguokui 2018/1/23
 */
public class ReportTaskStatusRequest extends BaseDataRequest<String> {
    // 设备ID
    private String deviceID;
    // 用户ID
    private String userId;
    // 任务ID
    private String taskID;
    // 任务状态1：正在执行 2：时间范围内结束任务 3超出时间，强制结束
    private String taskStatus;
    // 结果状态 0正常  1异常（巡查点有超时）
    private String carryStatus;
    // 异常原因
    private String abnormalReason;

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        map.put("deviceID", deviceID);
        map.put("userId", userId);
        map.put("taskID", taskID);
        map.put("taskStatus", taskStatus);
        map.put("carryStatus", carryStatus);
        map.put("abnormalReason", abnormalReason);

        return map;
    }

    @Override
    protected String getCommand() {
        return DeviceCmd.PAT_TASK_STATUS;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getCarryStatus() {
        return carryStatus;
    }

    public void setCarryStatus(String carryStatus) {
        this.carryStatus = carryStatus;
    }

    public String getAbnormalReason() {
        return abnormalReason;
    }

    public void setAbnormalReason(String abnormalReason) {
        this.abnormalReason = abnormalReason;
    }
}

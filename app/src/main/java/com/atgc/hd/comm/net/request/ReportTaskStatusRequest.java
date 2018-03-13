package com.atgc.hd.comm.net.request;

import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.net.request.base.BaseRequest;
import com.atgc.hd.comm.net.response.base.BaseResponse;

/**
 * <p>描述：8.3.33	上报巡查任务状态
 * <p>作者：liangguokui 2018/1/23
 */
public class ReportTaskStatusRequest extends BaseRequest {
    // 设备ID
    private String deviceID;
    // 用户ID
    private String userId;
    // 任务ID
    private String taskID;
    // 任务状态 0:未下发 1: 未执行 2:正在执行 3:时间范围内结束任务 4：强制结束任务 5:解除异常
    private String taskStatus;
    // 结果状态 0正常  1异常（巡查点有超时）
    private String carryStatus;
    // 异常原因
    private String abnormalReason;

    public ReportTaskStatusRequest() {
        if (Constants.isDemo) {
            serialNum = "s5d9w2da61fa6d5e8sdsa";
        }
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

    @Override
    public String getRequestCommand() {
        return DeviceCmd.PAT_TASK_STATUS;
    }

    @Override
    public String getResponseCommand() {
        return DeviceCmd.PAT_TASK_STATUS;
    }

    @Override
    public Class<?> getResponseClass() {
        return BaseResponse.class;
    }
}

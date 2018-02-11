package com.atgc.hd.comm.net.request;

import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.net.request.base.BaseRequest;
import com.atgc.hd.comm.net.response.base.BaseResponse;

/**
 * <p>描述：8.3.30 上报巡查点结果
 * <p>作者：liangguokui 2018/1/23
 */
public class ReportPointStatusRequest extends BaseRequest {
    // 设备ID
    private String deviceID;
    // 任务ID
    private String taskID;
    // 巡更点ID
    private String taskPointID;
    // 发生时间，格式 “yyyy-MM-dd HH:mm:ss”
    private String pointTime;

    private String planTime;
    // 结果类型 1.已巡查 2：超时未巡查 3.超时已巡查
    private String historyPointStatus;


    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTaskPointID() {
        return taskPointID;
    }

    public void setTaskPointID(String taskPointID) {
        this.taskPointID = taskPointID;
    }

    public String getPointTime() {
        return pointTime;
    }

    public void setPointTime(String pointTime) {
        this.pointTime = pointTime;
    }

    public String getHistoryPointStatus() {
        return historyPointStatus;
    }

    public void setHistoryPointStatus(String historyPointStatus) {
        this.historyPointStatus = historyPointStatus;
    }

    public String getPlanTime() {
        return planTime;
    }

    public void setPlanTime(String planTime) {
        this.planTime = planTime;
    }

    @Override
    public String getRequestCommand() {
        return DeviceCmd.PAT_POINT_RESULT;
    }

    @Override
    public String getResponseCommand() {
        return DeviceCmd.PAT_POINT_RESULT;
    }

    @Override
    public Class<?> getResponseClass() {
        return BaseResponse.class;
    }
}

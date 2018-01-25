package com.atgc.hd.client.tasklist.patrolfrag.adapter;

/**
 * <p>描述： 8.3.25	下发巡更任务
 * <p>作者： liangguokui 2018/1/18
 */
public class TaskEntity {
    // 设备编号
    private String deviceID;
    // 计划任务ID
    private String taskID;
    // 开始时间，格式“yyyy-MM-dd HH:mm:ss”
    private String startTime;
    // 巡更点信息
    private String PointArray;
    // 服务器当前时间，格式“yyyy-MM-dd HH:mm:ss”
    private String nowTime;
    // 是否已打点
    private boolean isChecked;

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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getPointArray() {
        return PointArray;
    }

    public void setPointArray(String pointArray) {
        PointArray = pointArray;
    }

    public String getNowTime() {
        return nowTime;
    }

    public void setNowTime(String nowTime) {
        this.nowTime = nowTime;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}

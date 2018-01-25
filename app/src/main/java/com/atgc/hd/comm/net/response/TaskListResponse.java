package com.atgc.hd.comm.net.response;

import java.util.List;

/**
 * <p>描述：网关下发的巡更任务
 * <p>作者：liangguokui 2018/1/23
 */
public class TaskListResponse {
    private List<TaskInfo> TaskArray;

    public static class TaskInfo {
        // 设备编号
        private String deviceID;
        // 计划任务ID
        private String taskID;
        // 开始时间，格式“yyyy-MM-dd HH:mm:ss”
        private String startTime;
        // 结束时间，格式“yyyy-MM-dd HH:mm:ss”
        private String endTime;
        // 巡更点信息
        private List<PointInfo> PointArray;

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

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public List<PointInfo> getPointArray() {
            return PointArray;
        }

        public void setPointArray(List<PointInfo> pointArray) {
            PointArray = pointArray;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    public static class PointInfo {
        // 巡更点ID
        private String pointId;
        // 巡更点名称
        private String pointName;
        // 时间间隔
        private String interval;
        // 次序
        private String orderNo;
        // 经度(84坐标系)
        private double longitude;
        // 纬度(84坐标系)
        private double latitude;
        // 卡号
        private String cardNumber;
        // 结果类型 0：未巡查1：正常（已巡查） 2：超时未巡查 3：超时已巡查
        private String resultType;

        private boolean isChecked;

        public String getPointId() {
            return pointId;
        }

        public void setPointId(String pointId) {
            this.pointId = pointId;
        }

        public String getPointName() {
            return pointName;
        }

        public void setPointName(String pointName) {
            this.pointName = pointName;
        }

        public String getInterval() {
            return interval;
        }

        public void setInterval(String interval) {
            this.interval = interval;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getResultType() {
            return resultType;
        }

        public void setResultType(String resultType) {
            this.resultType = resultType;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }
    }

    public List<TaskInfo> getTaskArray() {
        return TaskArray;
    }

    public void setTaskArray(List<TaskInfo> taskArray) {
        TaskArray = taskArray;
    }
}

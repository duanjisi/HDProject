package com.atgc.hd.comm.net.response;

import android.support.annotation.NonNull;

import com.atgc.hd.comm.utils.DateUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>描述：网关下发的巡更任务
 * <p>作者：liangguokui 2018/1/23
 */
public class TaskListResponse implements Serializable {
    private List<TaskInfo> TaskArray;

    public static class TaskInfo implements Serializable, Comparable<TaskListResponse.TaskInfo> {
        /**
         * 表示任务未执行
         */
        public static final String STATUS_UNDO = "STATUS_UNDO";
        /**
         * 表示任务进行中
         */
        public static final String STATUS_DOING = "STATUS_DOING";
        /**
         * 表示任务已过了完成时间
         */
        public static final String STATUS_DONE = "STATUS_DONE";

        // 设备编号
        private String deviceID;
        // 计划任务ID
        private String taskID;
        // 开始时间，格式“yyyy-MM-dd HH:mm:ss”
        private String startTime;
        // 结束时间，格式“yyyy-MM-dd HH:mm:ss”
        private String endTime;
        // 任务名称
        private String taskName;
        // 巡更点信息
        private List<PointInfo> PointArray;

        private String taskPeriod;

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

        public String getTaskName() {
            return taskName;
        }

        public void setTaskName(String taskName) {
            this.taskName = taskName;
        }

        public String getTaskPeriod() {

            return taskPeriod;
        }

        public void setTaskPeriod(String taskPeriod) {
            this.taskPeriod = taskPeriod;
        }

        public void initTaskPeriod() {
            String start = getStartTime().substring(10, 16);
            String end = getEndTime().substring(10, 16);

            setTaskPeriod(start + " -" + end);
        }

        /**
         * 根据传入的时间判断当前任务的状态
         *
         * @param currentDate 当前时间 yyyy-MM-dd HH:mm:ss
         * @return
         */
        public String taskStatus(Date currentDate) {

            Date startDate = DateUtil.dateParse(startTime, DateUtil.DATE_TIME_PATTERN);
            Date endDate = DateUtil.dateParse(endTime, DateUtil.DATE_TIME_PATTERN);

            // 任务未开始
            if (currentDate.before(startDate)) {
                return STATUS_UNDO;
            }
            // 任务进行中
            else if (currentDate.after(startDate) && currentDate.before(endDate)) {
                return STATUS_DOING;
            }
            // 任务已过最后时间
            else {
                return STATUS_DONE;
            }
        }

        @Override
        public int compareTo(@NonNull TaskInfo o) {
            return this.getStartTime().compareTo(o.getStartTime());
        }
    }

    public static class PointInfo implements Serializable, Comparable<TaskListResponse.PointInfo> {
        // 巡更点ID
        private String taskPointId;
        // 巡更点名称
        private String pointName;
        // 时间间隔
        private int interval;
        // 次序
        private int orderNo;
        // 经度(84坐标系)
        private double longitude;
        // 纬度(84坐标系)
        private double latitude;
        // 卡号
        private String cardNumber;
        // 结果类型 0：未巡查1：正常（已巡查） 2：超时未巡查 3：超时已巡查
        private String resultType;

        // 实际打点时间
        private String checkedTime;

        private boolean isChecked;

        public String getTaskPointId() {
            return taskPointId;
        }

        public void setTaskPointId(String taskPointId) {
            this.taskPointId = taskPointId;
        }

        public String getPointName() {
            return pointName;
        }

        public void setPointName(String pointName) {
            this.pointName = pointName;
        }

        public int getInterval() {
            return interval;
        }

        public void setInterval(int interval) {
            this.interval = interval;
        }

        public int getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(int orderNo) {
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

        public String getCheckedTime() {
            return checkedTime;
        }

        public void setCheckedTime(String checkedTime) {
            this.checkedTime = checkedTime;
        }

        @Override
        public int compareTo(@NonNull PointInfo o) {
            boolean temp = this.getOrderNo() > o.getOrderNo();
            return temp ? 1 : 0;
        }
    }

    public List<TaskInfo> getTaskArray() {
        return TaskArray;
    }

    public void setTaskArray(List<TaskInfo> taskArray) {
        TaskArray = taskArray;
    }
}

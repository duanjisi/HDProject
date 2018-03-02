package com.atgc.hd.comm.net.response;

import android.support.annotation.NonNull;

import com.alibaba.fastjson.annotation.JSONField;
import com.atgc.hd.comm.net.response.base.BaseResponse;
import com.atgc.hd.comm.utils.DateUtil;
import com.atgc.hd.comm.utils.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>描述：网关下发的巡更任务
 * <p>作者：liangguokui 2018/1/23
 */
public class TaskListResponse extends BaseResponse<TaskListResponse.TaskInfo> implements Serializable {

    public static class TaskInfo implements Serializable, Comparable<TaskInfo> {
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
        // 用户id
        private String userId;
        // 计划任务ID
        private String taskID;
        // 任务名称
        private String taskName;
        // 开始时间，格式“yyyy-MM-dd HH:mm:ss”
        private String startTime;
        // 结束时间，格式“yyyy-MM-dd HH:mm:ss”
        private String endTime;
        // 巡更点信息
        @JSONField(name = "data")
        private List<PointInfo> PointArray;
        // 任务状态，0-未下发 1-未执行 2-正在执行 3-时间范围内结束任务 4-强制结束任务 5-解除异常
        private String taskStatus;

        private String taskPeriod;

        private Date startTimeDate;

        private Date endTimeDate;

        // 0-有部分点未巡查 1-所有点已正常巡查 2-所有点已巡查但有异常点
        private String inspectStatus = "1";

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

        public String getStartTime() {
            return startTime;
        }

        public Date taskStartTime() {
            if (startTimeDate == null) {
                startTimeDate = DateUtil.dateParse(getStartTime());
            }

            return startTimeDate;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
            startTimeDate = DateUtil.dateParse(getStartTime());
        }

        public String getEndTime() {
            return endTime;
        }

        public Date taskEndTime() {
            if (endTimeDate == null) {
                endTimeDate = DateUtil.dateParse(getEndTime());
            }
            return endTimeDate;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
            endTimeDate = DateUtil.dateParse(getEndTime());
        }

        public List<PointInfo> getPointArray() {
            return PointArray;
        }

        public void setPointArray(List<PointInfo> pointArray) {
            PointArray = pointArray;
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

        public String getInspectStatus() {
            return inspectStatus;
        }

        public void setInspectStatus(String inspectStatus) {
            this.inspectStatus = inspectStatus;
        }

        public void initInspectStatus() {
            // 结果类型 0：未巡查 1：正常（已巡查） 2：超时未巡查 3：超时已巡查
            int countUnCheck = 0;
            int countChecked = 0;
            int countUnCheckException = 0;
            int countCheckedException = 0;
            for (PointInfo pointInfo : PointArray) {
                String resultType = pointInfo.getResultType();
                if ("0".equals(resultType)) {
                    countUnCheck++;
                } else if ("1".equals(resultType)) {
                    countChecked++;
                } else if ("2".equals(resultType)) {
                    countUnCheckException++;
                } else if ("3".equals(resultType)) {
                    countCheckedException++;
                }
            }

            // 0-有部分点未巡查
            // 1-所有点已正常巡查
            // 2-所有点已巡查但有异常点或其他异常情况
            if (countUnCheck > 0 || countUnCheckException > 0) {
                inspectStatus = "0";
            } else if (countChecked == PointArray.size()) {
                inspectStatus = "1";
            } else if ((countChecked + countCheckedException) == PointArray.size()) {
                inspectStatus = "2";
            } else {
                inspectStatus = "2";
            }
        }

        public void initTaskPeriod() {
            String start = getStartTime().substring(10, 16);
            String end = getEndTime().substring(10, 16);

            setTaskPeriod(start + " -" + end);
        }

        public String getTaskStatus() {
            return taskStatus;
        }

        public void setTaskStatus(String taskStatus) {
            this.taskStatus = taskStatus;
        }

        /**
         * 根据传入的时间判断当前任务的状态
         *
         * @param currentDate 当前时间 yyyy-MM-dd HH:mm:ss
         * @return
         */
        public String initTaskStatus(Date currentDate) {
            if ("1".equals(inspectStatus) || "2".equals(inspectStatus)) {
                taskStatus = STATUS_DONE;
                return taskStatus;
            }

            Date startDate = DateUtil.dateParse(startTime, DateUtil.DATE_TIME_PATTERN);
            Date endDate = DateUtil.dateParse(endTime, DateUtil.DATE_TIME_PATTERN);

            // 任务未开始
            if (currentDate.before(startDate)) {
                taskStatus = STATUS_UNDO;
            }
            // 任务进行中
            else if (currentDate.after(startDate) && currentDate.before(endDate)) {
                taskStatus = STATUS_DOING;
            }
            // 任务已过最后时间
            else {
                taskStatus = STATUS_DONE;
            }
            return taskStatus;
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
        // 结果类型 0：未巡查 1：正常（已巡查） 2：超时未巡查 3：超时已巡查
        private String resultType;

        // 实际打点时间 yyyy-MM-dd HH:mm:ss
        private String pointTime;
        // 最迟打点时间 yyyy-MM-dd HH:mm:ss
        private String planTime;

        private boolean isChecked;

        private String formatPointTime;
        private String formatPlanTime;

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

        public String getPointTime() {
            return pointTime == null ? "" : pointTime;
        }

        public String getFormatPointTime() {
            if (formatPointTime == null) {
                if (StringUtils.isNotEmpty(pointTime)) {
                    formatPointTime = pointTime.substring(11, 16);
                    return formatPointTime;
                }
                return "--:--";
            } else {
                return formatPointTime;
            }
        }

        public void setPointTime(String pointTime) {
            this.pointTime = pointTime;
        }

        public String getPlanTime() {
            return planTime;
        }

        public String getFormatPlanTime() {
            if (formatPlanTime == null) {
                if (StringUtils.isNotEmpty(planTime)) {
                    formatPlanTime = planTime.substring(11, 16);
                    return formatPlanTime;
                }
                return "--:--";
            } else {
                return formatPlanTime;
            }
        }

        public void setPlanTime(String planTime) {
            this.planTime = planTime;
        }

        @Override
        public int compareTo(@NonNull PointInfo o) {
            boolean temp = this.getOrderNo() > o.getOrderNo();
            return temp ? 1 : 0;
        }
    }
}

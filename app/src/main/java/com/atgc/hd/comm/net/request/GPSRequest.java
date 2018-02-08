/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.net.request;

import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.net.BaseDataRequest;
import com.atgc.hd.comm.utils.DateUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>描述：
 * <p>作者：duanjisi 2018年 01月 17日
 */

public class GPSRequest extends BaseDataRequest<String> {

    private String deviceID = "137000000000000000";
    private String uploadTime = "";
    private String longitude = "";
    private String latitude = "";
    private String direction = "";
    private String speed = "";
    private String satellites = "";
    private String precision = "";
    private String height = "";
    private String retransFlag = "0";
    private String needsResponse = "1";
    private String remark = "";
    private String userID = "";
    private String taskId = "";

    public GPSRequest() {
        this.uploadTime = DateUtil.currentTime();
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        map.put("deviceID", deviceID);
        map.put("uploadTime", uploadTime);
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("direction", direction);
        map.put("speed", speed);
        map.put("satellites", satellites);
        map.put("precision", precision);
        map.put("height", height);
        map.put("retransFlag", retransFlag);
        map.put("needsResponse", needsResponse);
        map.put("remark", remark);
        map.put("userID", userID);
        map.put("taskID", taskId);
        return map;
    }

    @Override
    protected String getCommand() {
        return DeviceCmd.PAT_UPLOAD_GPS;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getSatellites() {
        return satellites;
    }

    public void setSatellites(String satellites) {
        this.satellites = satellites;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getRetransFlag() {
        return retransFlag;
    }

    public void setRetransFlag(String retransFlag) {
        this.retransFlag = retransFlag;
    }

    public String getNeedsResponse() {
        return needsResponse;
    }

    public void setNeedsResponse(String needsResponse) {
        this.needsResponse = needsResponse;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}

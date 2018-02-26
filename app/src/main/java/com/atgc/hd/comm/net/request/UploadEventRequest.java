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
import com.atgc.hd.comm.net.request.base.BaseRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>描述：上报应急事件
 * <p>作者：duanjisi 2018年 01月 19日
 */

public class UploadEventRequest extends BaseRequest {
    public String deviceID;
    public String longitude;
    public String latitude;
    public String uploadTime;
    public String description;
    public String picUrl;
    public String videoUrl;
    public String place;
    public String eventType;

    @Override
    public String getRequestCommand() {
        return DeviceCmd.UP_LOAD_EMERGENCY;
    }

    @Override
    public String getResponseCommand() {
        return DeviceCmd.UP_LOAD_EMERGENCY;
    }

    @Override
    public Class<?> getResponseClass() {
        return null;
    }
}

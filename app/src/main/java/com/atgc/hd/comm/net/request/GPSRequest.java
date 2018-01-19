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

import java.util.HashMap;
import java.util.Map;

/**
 * <p>描述：
 * <p>作者：duanjisi 2018年 01月 17日
 */

public class GPSRequest extends BaseDataRequest<String> {
    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> map = new HashMap<>();
        map.put("deviceID", "10012017020000000000");
        map.put("uploadTime", "2018-1-17 09:00:42");
        map.put("longitude", "113.468761313");
        map.put("latitude", "23.5468321315");
        map.put("direction", "");
        map.put("speed", "");
        map.put("satellites", "");
        map.put("precision", "");
        map.put("height", "");
        map.put("retransFlag", "0");
        map.put("needsResponse", "0");
        map.put("remark", "");
        map.put("userID", "357684684634234");
        map.put("taskID", "548894889558555");
        return map;
    }

    @Override
    protected String getCommand() {
        return DeviceCmd.PAT_UPLOAD_GPS;
    }
}

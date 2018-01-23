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
 * <p>描述：上报应急事件
 * <p>作者：duanjisi 2018年 01月 19日
 */

public class UploadEventRequest extends BaseDataRequest<String> {

    public UploadEventRequest(String tag, Object... params) {
        super(tag, params);
    }

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        Map<String, String> map = (Map<String, String>) mParams[0];
        return map;
    }

    @Override
    protected String getCommand() {
        return DeviceCmd.UP_LOAD_EMERGENCY;
    }
}

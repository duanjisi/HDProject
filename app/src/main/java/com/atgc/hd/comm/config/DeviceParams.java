/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.config;
import android.content.Context;
import android.text.TextUtils;
import com.atgc.hd.HDApplication;
import com.atgc.hd.comm.Utils;
/**
 * <p>描述：设备参数
 * <p>作者：duanjisi 2018年 02月 06日
 */

public class DeviceParams {
    private static DeviceParams deviceParams = null;
    private String deviceId;
    private Context mContext;

    public DeviceParams() {
        mContext = HDApplication.getInstance().getApplicationContext();
    }

    public static DeviceParams getInstance() {
        if (deviceParams == null) {
            synchronized (DeviceParams.class) {
                if (deviceParams == null) {
                    deviceParams = new DeviceParams();
                }
            }
        }
        return deviceParams;
    }

    public String getDeviceId() {
//        10012017020000000000
//        10012017001899000022
//        10012017f6d0101be5ed
//        if (TextUtils.isEmpty(deviceId)) {
//            deviceId = Utils.getMacAddressFromIp(mContext);
//        }
//        return deviceId;
        return "10012017020000000000";
    }
}

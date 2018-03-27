/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.config;

/**
 * <p>描述：设备参数
 * <p>作者：duanjisi 2018年 02月 06日
 */

public class DeviceParams {
    private static DeviceParams deviceParams = null;
    private String deviceId = "10012017020000000000";
    private String aeskey = "";

    private DeviceParams() {
        resetAESkey();
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
        return deviceId;
    }

    public void setAESkey(String aeskey) {
        this.aeskey = aeskey;
    }

    public String getAESkey() {
        return aeskey;
    }

    public void resetAESkey() {
        aeskey = deviceId.substring(4);
    }
}

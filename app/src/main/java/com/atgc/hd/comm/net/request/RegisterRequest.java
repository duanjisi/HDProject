package com.atgc.hd.comm.net.request;

import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.net.request.base.BaseRequest;

/**
 * Created by duanjisi on 2018/1/15.
 */

public class RegisterRequest extends BaseRequest {
    public String Type = "1";
    public String deviceID = DeviceParams.getInstance().getDeviceId();
    public String manufacturer = "XXX厂商";
    public String macNO = "102";
    public String locationAddr = "南门停车场入口";
    public String name = "停车场设备";
    public String ip = "172.16.10.22";
    public String gateWay = "00000000000000000";
    public String mac = "00:FF:81:99:2F";
    public String mask = "255.255.255.0";
    public String version = "V1.0.16_20171225001";

    @Override
    public String getRequestCommand() {
        return DeviceCmd.REGISTER;
    }

    @Override
    public String getResponseCommand() {
        return DeviceCmd.REGISTER;
    }

    @Override
    public Class<?> getResponseClass() {
        return null;
    }

}

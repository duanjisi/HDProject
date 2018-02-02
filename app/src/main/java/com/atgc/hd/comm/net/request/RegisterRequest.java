package com.atgc.hd.comm.net.request;

import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.net.BaseDataRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by duanjisi on 2018/1/15.
 */

public class RegisterRequest extends BaseDataRequest<String> {

    @Override
    protected boolean isParse() {
        return false;
    }

    @Override
    protected Map<String, String> getParams() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Type", "1");
        map.put("deviceID", "10012017020000000000");
        map.put("manufacturer", "XXX厂商");
        map.put("macNO", "102");
        map.put("locationAddr", "南门停车场入口");
        map.put("name", "停车场设备");
        map.put("ip", "172.16.10.22");
        map.put("gateWay", "00000000000000000");
        map.put("mac", "00:FF:81:99:2F");
        map.put("mask", "255.255.255.0");
        map.put("version", "V1.0.16_20171225001");
        return map;
    }

    @Override
    protected String getCommand() {
        return DeviceCmd.REGISTER;
    }
}

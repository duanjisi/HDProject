package com.atgc.hd.comm.socket;


import com.atgc.hd.comm.config.DeviceParams;
import com.hdsocket.net.request.BaseRequest;
import com.hdsocket.net.response.GetTaskListResp;

/**
 * <p>描述：查询机器状态
 * <p>作者：liangguokui 2018/2/7
 */
public class RegisterEntity extends BaseRequest {
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
        return "COM_DEV_REGISTER";
    }

    @Override
    public String getResponseCommand() {
        return "COM_DEV_REGISTER";
    }

    @Override
    public Class<?> getResponseClass() {
        return null;
    }

}

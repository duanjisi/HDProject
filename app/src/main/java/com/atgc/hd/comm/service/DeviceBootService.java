/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.IPPort;
import com.atgc.hd.comm.PrefKey;
import com.atgc.hd.comm.local.LocationService;
import com.atgc.hd.comm.net.BaseDataRequest;
import com.atgc.hd.comm.net.PreRspPojo;
import com.atgc.hd.comm.net.TcpSocketClient;
import com.atgc.hd.comm.net.request.GPSRequest;
import com.atgc.hd.comm.utils.DigitalUtils;
import com.atgc.hd.comm.utils.PreferenceUtils;
import com.atgc.hd.entity.ActionEntity;
import com.baidu.location.BDLocation;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * <p>描述：设备引导服务
 * <p>作者：duanjisi 2018年 01月 16日
 */
public class DeviceBootService extends Service implements TcpSocketClient.TcpListener {

    private LocationService locationService;
    private TcpSocketClient tcpSocketClient = null;
    private Timer timer = null;
    private TimerTask heartBeatTimerTask = new TimerTask() {
        @Override
        public void run() {
            sendHeatBeat();
        }
    };
    private TimerTask gpsTimerTask = new TimerTask() {
        @Override
        public void run() {
            sendGps();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        timer = new Timer();

        tcpSocketClient = TcpSocketClient.getInstance();
        tcpSocketClient.setListener(this);
        if (!tcpSocketClient.isConnected()) {
            tcpSocketClient.connect(IPPort.getHOST(), IPPort.getPORT());
        }

        Logger.e("开机广播服务");

        locationService = LocationService.intance();
        locationService.initService(this);
        locationService.start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Logger.i("info===============开启服务onStart");
        sendRegisterMsg();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendRegisterMsg() {
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
        byte[] datas = DigitalUtils.getBytes(DeviceCmd.REGISTER, map);
        tcpSocketClient.sendMsg(datas);
    }

    private void sendHeatBeat() {
        byte[] datas = DigitalUtils.getBytes(DeviceCmd.HEART_BEAT, null);
        tcpSocketClient.sendMsg(datas);
    }

    private void startHeartBeat() {
        timer.schedule(heartBeatTimerTask, 1000, 60 * 1000);
    }

    private void sendGps() {
        BDLocation bdLocation = locationService.getLastBDLocation();
        if (bdLocation == null) {
            return;
        }

        GPSRequest gpsRequest = new GPSRequest();
        gpsRequest.setLongitude("" + bdLocation.getLongitude());
        gpsRequest.setLatitude("" + bdLocation.getLatitude());

        gpsRequest.send(new BaseDataRequest.RequestCallback() {
            @Override
            public void onSuccess(Object pojo) {

            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }

    private void startSendGps() {
        timer.schedule(gpsTimerTask, 0, 10 * 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tcpSocketClient.disConnected();
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onConnectBreak() {
        EventBus.getDefault().post(new ActionEntity(Constants.Action.CONNECT_BREAK));
    }

    @Override
    public void onReceive(PreRspPojo preRspPojo) {
        if (preRspPojo.Result.equals("0")) {
            //设备注册成功
            if (preRspPojo.Command.equals(DeviceCmd.REGISTER)) {
                PreferenceUtils.putBoolean(this, PrefKey.REGISTER, true);
                EventBus.getDefault().post(new ActionEntity(Constants.Action.REGISTER_SUCCESSED, 0));

                startHeartBeat();
                startSendGps();
                Logger.i("info===============设备注册成功");
            } else if (preRspPojo.Command.equals(DeviceCmd.HEART_BEAT)) {
                EventBus.getDefault().post(new ActionEntity(Constants.Action.HEART_BEAT, 0));
                Logger.i("info===============心跳包响应成功");
            }
        }
        //响应失败
        else {
            //设备注册失败
            if (preRspPojo.Command.equals(DeviceCmd.REGISTER)) {
                EventBus.getDefault().post(new ActionEntity(Constants.Action.REGISTER_SUCCESSED, 1));
                startHeartBeat();
                Logger.i("info===============设备注册失败");
            } else if (preRspPojo.Command.equals(DeviceCmd.HEART_BEAT)) {
                EventBus.getDefault().post(new ActionEntity(Constants.Action.HEART_BEAT, 1));
                Logger.i("info===============心跳包响应失败");
            }
        }
    }

    @Override
    public void onConnectFalied() {
        EventBus.getDefault().post(new ActionEntity(Constants.Action.CONNECT_FALIED));
    }

    @Override
    public void onSendSuccess(byte[] s) {

    }
}

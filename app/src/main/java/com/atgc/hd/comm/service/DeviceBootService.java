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
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.Ip_Port;
import com.atgc.hd.comm.PrefKey;
import com.atgc.hd.comm.net.PreRspPojo;
import com.atgc.hd.comm.net.SocketClientHandler;
import com.atgc.hd.comm.net.TcpSocketClient;
import com.atgc.hd.comm.utils.DigitalUtils;
import com.atgc.hd.comm.utils.PreferenceUtils;
import com.atgc.hd.entity.ActionEntity;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * <p>描述：设备引导服务
 * <p>作者：duanjisi 2018年 01月 16日
 */

public class DeviceBootService extends Service implements TcpSocketClient.TcpListener {

    private TcpSocketClient tcpSocketClient = null;
    private Timer timer = null;
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            sendHeatBeat();
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
        tcpSocketClient = TcpSocketClient.getInstance();
        tcpSocketClient.setListener(this);
        if (!tcpSocketClient.isConnected()) {
            tcpSocketClient.connect(Ip_Port.getHOST(), Ip_Port.getPORT());
        }
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        boolean isRegister = PreferenceUtils.getBoolean(this, PrefKey.REGISTER, false);
        if (!isRegister) {

        } else {

        }
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
        tcpSocketClient.getTransceiver().sendMSG(datas);
    }

    private void sendHeatBeat() {
        byte[] datas = DigitalUtils.getParamBytes(DeviceCmd.HEART_BEAT, null);
        tcpSocketClient.getTransceiver().sendMSG(datas);
    }

    private void startHeartBeat() {
        timer = new Timer();
        timer.schedule(timerTask, 1000, 1000 * 60);
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

    }

    @Override
    public void onReceive(String s) {

    }

    @Override
    public void onConnectFalied() {

    }

    @Override
    public void onSendSuccess(byte[] s) {

    }
}

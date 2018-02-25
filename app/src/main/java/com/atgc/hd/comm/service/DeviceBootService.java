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
import com.atgc.hd.comm.PrefKey;
import com.atgc.hd.comm.local.LocationService;
import com.atgc.hd.comm.net.PreRspPojo;
import com.atgc.hd.comm.socket.SocketManager;
import com.atgc.hd.comm.utils.PreferenceUtils;
import com.atgc.hd.entity.ActionEntity;
import com.orhanobut.logger.Logger;

import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * <p>描述：设备引导服务
 * <p>作者：duanjisi 2018年 01月 16日
 */
public class DeviceBootService extends Service {

    private LocationService locationService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        SocketManager.intance().initialize(getApplication());
        SocketManager.intance().initConfiguration();

        Logger.e("开机广播服务");
        locationService = LocationService.intance();
        locationService.initService(this);
//        locationService.start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        Logger.i("info===============开启服务onStart");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}

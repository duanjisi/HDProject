package com.atgc.hd;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.crash.CrashHandler;
import com.atgc.hd.comm.local.LocationService;
import com.atgc.hd.comm.socket.SocketManager;
import com.atgc.hd.comm.utils.FileUtil;
import com.atgc.hd.entity.Header;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.adapter.AndroidLogAdapter;
import com.orhanobut.logger.adapter.DiskLogAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>描述：
 * <p>作者： liangguokui 2018/1/12
 */

public class HDApplication extends Application {
    private static Context context;
    private static LocationService locationService;

    private static HDApplication mApplication;
    private Header header = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        context = getApplicationContext();

        initLog();

//        initCrashHandler();

        initSocket();

        initLocationServcie();
    }

    public synchronized static HDApplication getInstance() {
        return mApplication;
    }

    private void initSocket() {
        SocketManager.intance().initialize(this);
//        SocketManager.intance().initConfiguration();
    }

    /**
     * 获取协议头部信息
     *
     * @return
     */
    public Header getHeader() {
        if (header == null) {
            header = new Header();
            header.setVersion("HDXM");
            header.setSrcID(DeviceParams.getInstance().getDeviceId());
            header.setDestID("00000000000000000000");
            header.setRequest("1");
            header.setHold("0");
            header.setPackNo("172");
            header.setCrc("");
        }
        return header;
    }

    private void initLog() {
        Logger.addLogAdapter(new AndroidLogAdapter());
        Logger.addLogAdapter(new DiskLogAdapter(FileUtil.LOG_PATH));
    }

    private void initCrashHandler() {
        CrashHandler crashHandler = CrashHandler.instance();
        crashHandler.init(getApplicationContext());
    }

    private void initLocationServcie() {
//        locationService = new LocationService(getApplicationContext());

//        LocationService locationService = new LocationService(getApplicationContext());
//        locationService.registerListener(new BDAbstractLocationListener() {
//            @Override
//            public void onReceiveLocation(BDLocation bdLocation) {
//                Logger.e("1111地址：" + bdLocation.getAddrStr() + "\n经度：" + bdLocation.getLongitude() + " 纬度：" + bdLocation.getLatitude());
//            }
//        });
//        locationService.start();
    }

    public static Context applicationContext() {
        return context;
    }

    public static LocationService locationService() {
        return locationService;
    }
}

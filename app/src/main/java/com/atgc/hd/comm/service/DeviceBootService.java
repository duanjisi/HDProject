/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.atgc.hd.R;
import com.atgc.hd.client.platform.PlatformInfoActivity;
import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.PrefKey;
import com.atgc.hd.comm.local.LocationService;
import com.atgc.hd.comm.net.PreRspPojo;
import com.atgc.hd.comm.net.response.base.Response;
import com.atgc.hd.comm.socket.OnActionAdapter;
import com.atgc.hd.comm.socket.SocketManager;
import com.atgc.hd.comm.utils.PreferenceUtils;
import com.atgc.hd.comm.utils.StringUtils;
import com.atgc.hd.db.dao.PlatformInfoDao;
import com.atgc.hd.entity.ActionEntity;
import com.atgc.hd.entity.PatInfo;
import com.orhanobut.logger.Logger;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

/**
 * <p>描述：设备引导服务
 * <p>作者：duanjisi 2018年 01月 16日
 */
public class DeviceBootService extends Service {
    private static final String REQUEST_GROUP_TAG = StringUtils.getRandomString(20);

    private static final int notifyID = 1002;
    private PlatformInfoDao infoDao;
    private LocationService locationService;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        infoDao = PlatformInfoDao.getInstance();
        SocketManager.intance().initialize(getApplication());
        SocketManager.intance().initConfiguration();

        Logger.e("开机广播服务");
        locationService = LocationService.intance();
        locationService.initService(this);
//        locationService.start();

        registerOnReceiveListener();
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

    /**
     * 注册监听平台消息
     */
    private void registerOnReceiveListener() {
        SocketManager.intance().registertOnActionListener(REQUEST_GROUP_TAG, DeviceCmd.PAT_SEND_MESSAGE, PatInfo.class, new OnActionAdapter() {
            @Override
            public void onResponseSuccess(String cmd, String serialNum, Response response, Bundle bundle) {
                List<PatInfo> patInfos = response.dataArray;
                final PatInfo patInfo = patInfos.get(0);
                bindDatas(patInfo);
            }
        });
    }

    private void bindDatas(PatInfo platform) {
        if (platform != null) {
            infoDao.save(platform);
            sendNotification(getApplicationContext(), platform.getMessageContent());
        }
    }


    private void sendNotification(Context appContext, String notice) {
        String packageName = appContext.getApplicationInfo().packageName;
        NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        PackageManager packageManager = appContext.getPackageManager();
        String appname = (String) packageManager.getApplicationLabel(appContext.getApplicationInfo());
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
                .setSmallIcon(appContext.getApplicationInfo().icon)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);
        int notificationId = 0;


        Intent intent = new Intent(appContext, PlatformInfoActivity.class);
        notificationId = notifyID;

        mBuilder.setContentTitle(getString(R.string.platform));
        mBuilder.setTicker(notice);
        mBuilder.setContentText(notice);

        EventBus.getDefault().post(new ActionEntity(Constants.Action.PLATFORM_INFO));
        PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        Notification notification = mBuilder.build();
        notification.defaults = Notification.DEFAULT_SOUND;
        notificationManager.notify(notificationId, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.i("info===============服务onDestroy");
        SocketManager.intance().unRegistertOnActionListener(DeviceCmd.PAT_SEND_MESSAGE);
    }
}

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
import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.local.Coordinate;
import com.atgc.hd.comm.local.LocationService;
import com.atgc.hd.comm.net.request.GPSRequest;
import com.atgc.hd.comm.net.request.RegisterRequest;
import com.atgc.hd.comm.net.response.TaskListResponse;
import com.atgc.hd.comm.net.response.base.Response;
import com.atgc.hd.comm.socket.OnActionAdapter;
import com.atgc.hd.comm.socket.SocketManager;
import com.atgc.hd.comm.utils.CoordinateUtil;
import com.atgc.hd.comm.utils.StringUtils;
import com.atgc.hd.db.dao.PlatformInfoDao;
import com.atgc.hd.entity.ActionEntity;
import com.atgc.hd.entity.EventMessage;
import com.atgc.hd.entity.PatInfo;
import com.baidu.location.BDLocation;
import com.orhanobut.logger.Logger;
import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.SocketActionAdapter;

import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * <p>描述：设备引导服务
 * <p>作者：duanjisi 2018年 01月 16日
 */
public class DeviceBootService extends Service implements LocationService.ILocationListener {
    private static final String REQUEST_GROUP_TAG = StringUtils.getRandomString(20);

    private static final int notifyID = 1002;
    private static final String CHANNEL_ID = "paltform_msg_CHANNEL_ID";
    private LocationService locationService;
    private boolean isDeviceRegister = false;

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
        SocketManager.intance().registerSockActionAdapter(getSocketAction());
        SocketManager.intance().switchConnect();

        Logger.e("开机广播服务");
        locationService = LocationService.intance();
        locationService.initService(this);
        locationService.registerLocationListener(this);
        locationService.start();

        registerOnReceiveListener();

        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Logger.i("开启服务onStartCommand");

        return super.onStartCommand(intent, flags, startId);
    }

    private SocketActionAdapter getSocketAction() {
        return new SocketActionAdapter() {
            @Override
            public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
                registerDevice();
            }

            @Override
            public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
                isDeviceRegister = false;
            }

            @Override
            public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {
                isDeviceRegister = false;
            }
        };
    }

    private OnActionAdapter responseActionListener = new OnActionAdapter() {
        @Override
        public void onResponseSuccess(String cmd, String serialNum, Response response, Bundle bundle) {
            // 设备注册成功
            if (DeviceCmd.REGISTER.equals(cmd)) {
                isDeviceRegister = true;
                SocketManager.intance().startPulse();
                sendEventMessage("ready_to_next_aty", null);
            }
            // 接收平台下发的文本消息
            else if (DeviceCmd.PAT_SEND_MESSAGE.equals(cmd)) {
                List<PatInfo> patInfos = response.dataArray;
                PatInfo patInfo = patInfos.get(0);
                bindDatas(patInfo);
            }
        }
    };

    @Subscribe
    public void checkConnect(EventMessage msg) {
        if (!msg.checkTag("check_socket_connect")) {
            return;
        }

        Logger.w("检查socket是否已连接：" + SocketManager.intance().isSocketConnected());

        if (SocketManager.intance().isSocketConnected()) {
            checkDeviceRegister();
        } else {
            SocketManager.intance().switchConnect();
        }
    }

    public void checkDeviceRegister() {
        Logger.w("是否已注册：" + isDeviceRegister);
        if (isDeviceRegister) {
            sendEventMessage("ready_to_next_aty", null);
        } else {
            registerDevice();
        }
    }

    /**
     * 注册监听平台消息
     */
    private void registerOnReceiveListener() {
        // 注册监听
        SocketManager.intance().registertOnActionListener(
                REQUEST_GROUP_TAG,
                DeviceCmd.REGISTER,
                null,
                responseActionListener);

        SocketManager.intance().registertOnActionListener(
                REQUEST_GROUP_TAG,
                DeviceCmd.PAT_SEND_MESSAGE,
                PatInfo.class,
                responseActionListener);
        SocketManager.intance().preAnalysisResponseNoRequestTag(
                REQUEST_GROUP_TAG,
                DeviceCmd.PAT_SEND_MESSAGE,
                null);
    }

    // 设备注册
    private void registerDevice() {
        RegisterRequest request = new RegisterRequest();

        request.deviceID = DeviceParams.getInstance().getDeviceId();

        SocketManager.intance().launch(REQUEST_GROUP_TAG, request, null, true);
    }

    private String taskid = "";
    private String userid = "";

    @Subscribe
    public void currentTaskInfo(EventMessage eventMessage) {
        if (eventMessage.checkTag("current_task_info")) {
            if (eventMessage.object == null) {
                taskid = "";
                userid = "";
            } else {
                TaskListResponse.TaskInfo taskInfo = (TaskListResponse.TaskInfo) eventMessage.object;
                taskid = taskInfo.getTaskID();
                userid = taskInfo.getUserId();
            }
        }
    }

    // 设备上传
    private void uploadGps(String longitude, String latitude) {
        GPSRequest gpsRequest = new GPSRequest();
        gpsRequest.setDeviceID(DeviceParams.getInstance().getDeviceId());
        gpsRequest.setTaskId(taskid);
        gpsRequest.setUserID(userid);
        gpsRequest.setLongitude(longitude);
        gpsRequest.setLatitude(latitude);

        SocketManager.intance().launch(REQUEST_GROUP_TAG, gpsRequest, null);
    }

    private void bindDatas(PatInfo platform) {
        if (platform != null) {
            PlatformInfoDao.getInstance().save(platform);
            sendNotification(getApplicationContext(), platform.getMessageContent());
        }
    }

    private void sendNotification(Context appContext, String notice) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext, CHANNEL_ID);

        mBuilder.setSmallIcon(appContext.getApplicationInfo().icon);
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setAutoCancel(true);
        mBuilder.setContentTitle(getString(R.string.platform));
        mBuilder.setTicker(notice);
        mBuilder.setContentText(notice);

        Intent intent = new Intent(appContext, PlatformInfoActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(appContext, notifyID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        notification.defaults = Notification.DEFAULT_SOUND;

        NotificationManager notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifyID, notification);

        EventBus.getDefault().post(new ActionEntity(Constants.Action.PLATFORM_INFO));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationService.stop();
        SocketManager.intance().unRegistertOnActionListener(DeviceCmd.REGISTER);
        SocketManager.intance().unRegistertOnActionListener(DeviceCmd.PAT_SEND_MESSAGE);
        EventBus.getDefault().unregister(this);
    }

    private void sendEventMessage(String eventTag, Object object) {
        EventMessage msg = new EventMessage(eventTag, object);
        EventBus.getDefault().post(msg);
    }

    // 经纬度回调
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        Coordinate coordinate
                = CoordinateUtil.gcj02ToWgs84(bdLocation.getLongitude(), bdLocation.getLatitude());

        uploadGps(coordinate.getLongitudeStr(), coordinate.getLatitudeStr());
    }
}

package com.atgc.hd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.atgc.hd.activity.ConversationActivity;
import com.atgc.hd.activity.EmergencyEventActivity;
import com.atgc.hd.activity.EmergencyEventActivity;
import com.atgc.hd.activity.EmergencyListActivity;
import com.atgc.hd.activity.PlatformInfoActivity;
import com.atgc.hd.activity.SettingActivity;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.client.tasklist.TaskListActivity;
import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.PrefKey;
import com.atgc.hd.comm.Utils;
import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.local.GPSLocationTool;
import com.atgc.hd.comm.local.LocationService;
import com.atgc.hd.comm.net.BaseDataRequest;
import com.atgc.hd.comm.net.request.RegisterRequest;
import com.atgc.hd.comm.service.DeviceBootService;
import com.atgc.hd.comm.utils.PreferenceUtils;
import com.atgc.hd.entity.ActionEntity;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.orhanobut.logger.Logger;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class MainActivity extends BaseActivity {
    private TextView tv_net, tvResult, tvState, tv_register;
    private Handler mHandler = new Handler();
    private WifiManager my_wifiManager;
    private WifiInfo wifiInfo;
    private DhcpInfo dhcpInfo;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        my_wifiManager = ((WifiManager) getSystemService("wifi"));
        dhcpInfo = my_wifiManager.getDhcpInfo();
        wifiInfo = my_wifiManager.getConnectionInfo();
        initViews();
        Logger.e("MainActivity----------------------------------");
//        testGPSLocation();
//        testLocation();
    }

    private void testGPSLocation() {
        GPSLocationTool.isGpsEnabled(this);
        GPSLocationTool.isLocationEnabled(this);

        GPSLocationTool.registerLocation(this, 5 * 1000, 1, new GPSLocationTool.OnLocationChangeListener() {

            @Override
            public void getLastKnownLocation(Location location) {
                StringBuilder builder = new StringBuilder();
                builder.append("最后已知坐标\n经度：");
                builder.append(location.getLongitude());
                builder.append("\n纬度：");
                builder.append(location.getLatitude());
                tvResult.setText(builder.toString());

                Logger.e("最后已知坐标：经度-" + location.getLongitude() + " 纬度-" + location.getLatitude());
            }

            @Override
            public void onLocationChanged(Location location) {
                StringBuilder builder = new StringBuilder();
                builder.append("坐标位置变化\n经度：");
                builder.append(location.getLongitude());
                builder.append("\n纬度：");
                builder.append(location.getLatitude());
                tvResult.setText(builder.toString());

                Logger.e("坐标位置变化：经度-" + location.getLongitude() + " 纬度-" + location.getLatitude());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Logger.e("状态变化：" + provider + " " + status);
            }
        });
    }

    @Override
    public String toolBarTitle() {
        return "sdfasdf";
    }


    private BDAbstractLocationListener listener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            Logger.e("地址：" + bdLocation.getAddrStr() + "\n经度：" + bdLocation.getLongitude() + " 纬度：" + bdLocation.getLatitude());
//            经度：113.620759 纬度：23.305057           bd09ll
//            经度：113.614258 纬度：23.298979           WGS84？？
//            经度：113.614258 纬度：23.298979           gcj02
//            经度：1.2648342665247E7 纬度：2651895.153317   bd09
            StringBuilder builder = new StringBuilder();
            builder.append("经度：");
            builder.append(bdLocation.getLongitude());
            builder.append("\n纬度：");
            builder.append(bdLocation.getLatitude());
            tvResult.setText(builder.toString());
        }
    };

    @Subscribe
    public void onMessageEvent(ActionEntity event) {
        if (event != null) {
            String action = event.getAction();
            if (action.equals(Constants.Action.REGISTER_SUCCESSED)) {
                int tag = (int) event.getData();
                switch (tag) {
                    case 0:
                        showMessage("设备注册成功!");
                        break;
                    case 1:
                        showMessage("设备注册失败!");
                        break;
                }
            } else if (action.equals(Constants.Action.HEART_BEAT)) {
                int tag = (int) event.getData();
                switch (tag) {
                    case 0:
                        showMessage("心跳包来了!");
                        break;
                    case 1:
                        showMessage("心跳包响应失败!");
                        break;
                }
            } else if (action.equals(Constants.Action.CONNECT_BREAK)) {
                showMessage("连接断开!");
            } else if (action.equals(Constants.Action.CONNECT_FALIED)) {
                showMessage("连接失败!");
            }
        }
    }

    private void showMessage(final String string) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showToast(string, true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initViews() {
        tvResult = findViewById(R.id.tv_net);
        tv_net = findViewById(R.id.tv_start);
        tv_register = findViewById(R.id.tv_register);
        tvState = findViewById(R.id.tv_device_state);

//        boolean isRegister = PreferenceUtils.getBoolean(this, PrefKey.REGISTER, false);
//        if (!isRegister) {
//            Intent intent = new Intent(context, DeviceBootService.class);
//            startService(intent);
//        }

        tv_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PlatformInfoActivity.class));
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMac();
//                register();
            }
        });
//        Utils.printIpAddress();
    }

    private void getMac() {
        String mac = DeviceParams.getInstance().getDeviceId();
        showToast(mac);
    }


    private void register() {
        RegisterRequest request = new RegisterRequest();
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String json) {
                showToast(json);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg);
            }
        });
    }


}

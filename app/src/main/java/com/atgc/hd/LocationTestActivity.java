package com.atgc.hd;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.comm.local.Coordinate;
import com.atgc.hd.comm.local.GPSLocationTool;
import com.atgc.hd.comm.local.LocationService;
import com.atgc.hd.comm.utils.CoordinateUtil;
import com.atgc.hd.comm.utils.DigitalUtils;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

public class LocationTestActivity extends BaseActivity {
    private TextView tv_net, tvResult;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

//        testGPSLocation();
//        testLocation();

        Map<String, String> map = new HashMap<>();
        map.put("deviceID", "10012017020000000000");
        map.put("uploadTime", "2018-1-17 09:00:42");
        map.put("longitude", "113.468761313");
        map.put("latitude", "23.5468321315");
        map.put("direction", "");
        map.put("speed", "");
        map.put("satellites", "");
        map.put("precision", "");
        map.put("height", "");
        map.put("retransFlag", "0");
        map.put("needsResponse", "0");
        map.put("remark", "");
        map.put("userID", "357684684634234");
        map.put("taskID", "");

        DigitalUtils.getParamBytes("PAT_UPLOAD_GPS", map);
//        socketClientHandler.sendMsg("PAT_UPLOAD_GPS", map);
    }

    private void testGPSLocation() {
        GPSLocationTool.isGpsEnabled(this);
        GPSLocationTool.isLocationEnabled(this);

        GPSLocationTool.registerLocation(this, 1000, 1, new GPSLocationTool.OnLocationChangeListener() {

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

    LocationService service;

    private void testLocation() {
        service = HDApplication.locationService();
        service.registerListener(listener);
        service.setLocationOption(service.getDefaultLocationClientOption());

//        经度：113.6157 纬度：23.300853
//        notifyListener.SetNotifyLocation(23.300853, 113.615709, 2000, service.getOption().getCoorType());
//        service.registerNotifyListener(notifyListener);
        service.start();

    }

    private BDNotifyListener notifyListener = new BDNotifyListener() {

        @Override
        public void onNotify(BDLocation bdLocation, float v) {
            super.onNotify(bdLocation, v);
            Logger.e("\n位置到了\n" + bdLocation.getLongitude() + "\n" + bdLocation.getLatitude() + "\n 距离：" + v + "米");
            tvResult.append("\n位置到了\n" + bdLocation.getLongitude() + "\n" + bdLocation.getLatitude() + "\n 距离：" + v + "米");
        }
    };

    private BDAbstractLocationListener listener = new BDAbstractLocationListener() {
        BDLocation lastLocation = null;

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (lastLocation == null) {
                lastLocation = bdLocation;
            }
            double distance2 = CoordinateUtil.getDistance(lastLocation.getLatitude(), lastLocation.getLongitude(), bdLocation.getLatitude(), bdLocation.getLongitude());

            Logger.e("距离上一点：" + distance2 + "\n经度：" + bdLocation.getLongitude() + " 纬度：" + bdLocation.getLatitude());

            StringBuilder builder = new StringBuilder();
            builder.append("经度：");
            builder.append(bdLocation.getLongitude());
            builder.append("\n纬度：");
            builder.append(bdLocation.getLatitude());
            builder.append("\n类型：");
            builder.append(bdLocation.getCoorType());

            Coordinate coordinate = CoordinateUtil.gcj02ToWbs84(Double.valueOf(bdLocation.getLongitude()), Double.valueOf(bdLocation.getLatitude()));
            builder.append("\nWGS84: \n");
            builder.append(coordinate.getLongitude());
            builder.append("\n");
            builder.append(coordinate.getLatitude());

            tvResult.setText(builder.toString());

            Map<String, String> map = new HashMap<>();
            map.put("deviceID", "10012017020000000000");
            map.put("uploadTime", "2018-1-17 09:00:42");
            map.put("longitude", "113.468761313");
            map.put("latitude", "23.5468321315");
            map.put("direction", "");
            map.put("speed", "");
            map.put("satellites", "");
            map.put("precision", "");
            map.put("height", "");
            map.put("retransFlag", "0");
            map.put("needsResponse", "0");
            map.put("remark", "");
            map.put("userID", "357684684634234");
            map.put("taskID", "");

            socketClientHandler.sendMsg("PAT_UPLOAD_GPS", map);
        }
    };

    @Override
    protected void onDestroy() {
        service.unregisterListener(listener);
        service.unregisterNotifyListener(notifyListener);
        service.stop();

        super.onDestroy();
    }

    private void initViews() {
        tvResult = findViewById(R.id.tv_net);
        tv_net = findViewById(R.id.tv_start);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onError(String msg) {

    }

    @Override
    public void onResponse(String json) {

    }
}

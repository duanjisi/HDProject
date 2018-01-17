package com.atgc.hd.comm.local;

import android.content.Context;

import com.atgc.hd.comm.utils.CoordinateUtil;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author baidu
 */
public class LocationService {
    private LocationClient client = null;
    private LocationClientOption mOption;
    private LocationClientOption DIYoption;
    private Object objLock = new Object();
    private List<BDAbstractLocationListener> locationListeners;

    /***
     *
     * @param locationContext
     */
    public LocationService(Context locationContext) {
        synchronized (objLock) {
            if (client == null) {
                client = new LocationClient(locationContext);
                client.setLocOption(getDefaultLocationClientOption());
                locationListeners = new ArrayList<>();
            }
        }
    }

    private BDAbstractLocationListener bdAbstractLocationListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            double longitude = Double.valueOf(bdLocation.getLongitude());
            double latitude = Double.valueOf(bdLocation.getLatitude());
            Coordinate coordinate = CoordinateUtil.gcj02ToWbs84(longitude, latitude);
            bdLocation.setLongitude(coordinate.getLongitude());
            bdLocation.setLatitude(coordinate.getLatitude());

            bdLocation.setCoorType("已由gcj02转换为wgs84坐标系");
            for (BDAbstractLocationListener locationListener : locationListeners) {
                locationListener.onReceiveLocation(bdLocation);
            }
        }
    };

    /***
     *
     * @param listener
     * @return
     */

    public boolean registerListener(BDAbstractLocationListener listener) {
        boolean isSuccess = false;
        if (listener != null) {
            client.registerLocationListener(listener);
            isSuccess = true;
        }
        return isSuccess;
    }

    public void unregisterListener(BDAbstractLocationListener listener) {
        if (listener != null) {
            client.unRegisterLocationListener(listener);
        }
    }

    /**
     * 注册位置提醒
     *
     * @param listener
     * @return
     */
    public boolean registerNotifyListener(BDNotifyListener listener) {
        boolean isSuccess = false;
        client.registerNotify(listener);
        isSuccess = true;
        return isSuccess;
    }

    /**
     * 注销位置提醒
     *
     * @param listener
     */
    public void unregisterNotifyListener(BDNotifyListener listener) {
        if (listener != null) {
            client.removeNotifyEvent(listener);
        }
    }

    /***
     *
     * @param option
     * @return isSuccessSetOption
     */
    public boolean setLocationOption(LocationClientOption option) {
        boolean isSuccess = false;
        if (option != null) {
            if (client.isStarted())
                client.stop();
            DIYoption = option;
            client.setLocOption(option);
        }
        return isSuccess;
    }

    /***
     *
     * @return DefaultLocationClientOption  默认O设置
     */
    public LocationClientOption getDefaultLocationClientOption() {
        if (mOption == null) {
            mOption = new LocationClientOption();
            //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            mOption.setLocationMode(LocationMode.Device_Sensors);
            //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            mOption.setCoorType("gcj02");
            //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
            mOption.setScanSpan(5 * 1000);
            //可选，设置是否需要地址信息，默认不需要
            mOption.setIsNeedAddress(true);
            //可选，设置是否需要地址描述
            mOption.setIsNeedLocationDescribe(true);
            //可选，设置是否需要设备方向结果
            mOption.setNeedDeviceDirect(false);
            //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mOption.setIgnoreKillProcess(true);
            //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            mOption.setIsNeedLocationDescribe(false);
            //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            mOption.setIsNeedLocationPoiList(false);
            //可选，默认false，设置是否收集CRASH信息，默认收集
            mOption.SetIgnoreCacheException(false);
            //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            mOption.setLocationNotify(false);
            //可选，默认false，设置是否开启Gps定位
            mOption.setOpenGps(true);
            //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
            mOption.setIsNeedAltitude(true);
        }
        return mOption;
    }


    /**
     * @return DIYOption 自定义Option设置
     */
    public LocationClientOption getOption() {
        if (DIYoption == null) {
            DIYoption = new LocationClientOption();
        }
        return DIYoption;
    }

    public void start() {
        synchronized (objLock) {
            if (client != null && !client.isStarted()) {
                client.start();
            }
        }
    }

    /**
     * 请勿随意调用stop()方法，因有每隔10s获取经纬度上传的机制，调用stop()方法后所有地方都将获取不了经纬度
     */
    public void stop() {
        synchronized (objLock) {
            if (client != null && client.isStarted()) {
                client.stop();
            }
        }
    }

    public boolean isStart() {
        return client.isStarted();
    }

    public boolean requestHotSpotState() {
        return client.requestHotSpotState();
    }


}

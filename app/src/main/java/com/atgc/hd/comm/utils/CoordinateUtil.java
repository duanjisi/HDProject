package com.atgc.hd.comm.utils;

import com.atgc.hd.comm.local.Coordinate;

import java.text.DecimalFormat;

/**
 * <p>描述： 坐标转换工具
 * <p>作者： liangguokui 2018/1/16
 */
public class CoordinateUtil {
    public static double pi = 3.1415926535897932384626;
    public static double a = 6378245.0;
    public static double ee = 0.00669342162296594323;

    /**
     * GPS坐标 转换成 角度
     * 例如 113.202222 转换成 113°12′8″
     *
     * @param location
     * @return
     */
    public static String gpsToDegree(double location) {
        double degree = Math.floor(location);
        double minute_temp = (location - degree) * 60;
        double minute = Math.floor(minute_temp);
//        double second = Math.floor((minute_temp - minute)*60);
        String second = new DecimalFormat("#.##").format((minute_temp - minute) * 60);
        return (int) degree + "°" + (int) minute + "′" + second + "″";
    }

    /**
     * 国际 GPS84 坐标系
     * 转换成
     * [国测局坐标系] 火星坐标系 (GCJ-02)
     * <p>
     * World Geodetic System ==> Mars Geodetic System
     *
     * @param lon 经度
     * @param lat 纬度
     * @return GPS实体类
     */
    public static Coordinate wgs84ToGcj02(double lon, double lat) {
        if (outOfChina(lon, lat)) {
            return null;
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new Coordinate(mgLon, mgLat);
    }

    /**
     * [国测局坐标系] 火星坐标系 (GCJ-02)
     * 转换成
     * 国际 WbsS84 坐标系
     *
     * @param lon 火星经度
     * @param lat 火星纬度
     */
    public static Coordinate gcj02ToWbs84(double lon, double lat) {
        Coordinate gps = transform(lon, lat);
        double lontitude = lon * 2 - gps.getLongitude();
        double latitude = lat * 2 - gps.getLatitude();
        return new Coordinate(lontitude, latitude);
    }

    /**
     * 火星坐标系 (GCJ-02)
     * 转换成
     * 百度坐标系 (BD-09)
     *
     * @param gg_lon 经度
     * @param gg_lat 纬度
     */
    public static Coordinate gcj02ToBd09(double gg_lon, double gg_lat) {
        double x = gg_lon, y = gg_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * pi);
        double bd_lon = z * Math.cos(theta) + 0.0065;
        double bd_lat = z * Math.sin(theta) + 0.006;
        return new Coordinate(bd_lon, bd_lat);
    }

    /**
     * 百度坐标系 (BD-09)
     * 转换成
     * 火星坐标系 (GCJ-02)
     *
     * @param bd_lon 百度*经度
     * @param bd_lat 百度*纬度
     * @return GPS实体类
     */
    public static Coordinate bd09ToGcj02(double bd_lon, double bd_lat) {
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * pi);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * pi);
        double gg_lon = z * Math.cos(theta);
        double gg_lat = z * Math.sin(theta);
        return new Coordinate(gg_lon, gg_lat);
    }

    /**
     * 百度坐标系 (BD-09)
     * 转换成
     * 国际 GPS84 坐标系
     *
     * @param bd_lon 百度*经度
     * @param bd_lat 百度*纬度
     * @return GPS实体类
     */
    public static Coordinate bd09ToGps84(double bd_lon, double bd_lat) {
        Coordinate gcj02 = bd09ToGcj02(bd_lon, bd_lat);
        Coordinate map84 = gcj02ToWbs84(gcj02.getLongitude(), gcj02.getLatitude());
        return map84;

    }

    /**
     * 不在中国范围内
     *
     * @param lon 经度
     * @param lat 纬度
     * @return boolean值
     */
    public static boolean outOfChina(double lon, double lat) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        return lat < 0.8293 || lat > 55.8271;
    }

    /**
     * 转化算法
     *
     * @param lon
     * @param lat
     * @return
     */
    public static Coordinate transform(double lon, double lat) {
        if (outOfChina(lon, lat)) {
            return new Coordinate(lon, lat);
        }
        double dLat = transformLat(lon - 105.0, lat - 35.0);
        double dLon = transformLon(lon - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        double mgLat = lat + dLat;
        double mgLon = lon + dLon;
        return new Coordinate(mgLon, mgLat);
    }

    /**
     * 纬度转化算法
     *
     * @param x
     * @param y
     * @return
     */
    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    /**
     * 经度转化算法
     *
     * @param x
     * @param y
     * @return
     */
    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return ret;
    }

    private static double EARTH_RADIUS = 6378.137;
//                                       6378245.0

    /**
     * 通过经纬度获取距离(单位：米)
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
//        s = s * EARTH_RADIUS;
        s = s * CoordinateUtil.a;
        s = Math.round(s * 10000d) / 10000d;
//        s = s * 1000;
        return s;
    }


    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
}

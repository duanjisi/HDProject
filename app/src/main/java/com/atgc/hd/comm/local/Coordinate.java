package com.atgc.hd.comm.local;

/**
 * <p>描述：
 * <p>作者： liangguokui 2018/1/16
 */
public class Coordinate {

    private double mLatitude;
    private double mLongitude;

    public Coordinate(double longitude, double mLatitude) {
        setLatitude(mLatitude);
        setLongitude(longitude);
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    public String getLatitudeStr() {
        return String.valueOf(mLatitude);
    }
    public String getLongitudeStr() {
        return String.valueOf(mLongitude);
    }

    @Override
    public String toString() {
        return mLongitude + ", " + mLatitude;
    }
}

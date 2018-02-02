package com.atgc.hd.comm;

import android.text.TextUtils;

import com.atgc.hd.HDApplication;
import com.atgc.hd.comm.utils.PreferenceUtils;

/**
 * Created by duanjisi on 2018/1/12.
 */

public class IPPort {
    //    private static final String HOST = "172.16.10.80";
//    private static final String HOST = "172.16.10.127";
//    private static final String PORT = "20001";
    private static final String HOST = "172.16.10.127";
    private static final String PORT = "20001";

    public static String getHOST() {
        String ip = PreferenceUtils.getString(HDApplication.getInstance(), PrefKey.HOST, "");
        if (!TextUtils.isEmpty(ip)) {
            return ip;
        } else {
            return HOST;
        }
    }

    public static String getPORT() {
        String port = PreferenceUtils.getString(HDApplication.getInstance(), PrefKey.PORT, "");
        if (!TextUtils.isEmpty(port)) {
            return port;
        } else {
            return PORT;
        }
    }
}

package com.atgc.hd.comm;

import android.text.TextUtils;

import com.atgc.hd.HDApplication;
import com.atgc.hd.comm.utils.PreferenceUtils;

/**
 * Created by duanjisi on 2018/1/12.
 */

public class IPPort {
    private static final String HOST = "172.16.10.80";
    private static final int PORT = 20001;

    public static String getHOST() {
        String ip = PreferenceUtils.getString(HDApplication.getInstance(), PrefKey.HOST, "");
        if (!TextUtils.isEmpty(ip)) {
            return ip;
        } else {
            return HOST;
        }
    }

    public static int getPORT() {
        int port = PreferenceUtils.getInt(HDApplication.getInstance(), PrefKey.PORT, 0);
        if (port != 0) {
            return port;
        } else {
            return PORT;
        }
    }
}

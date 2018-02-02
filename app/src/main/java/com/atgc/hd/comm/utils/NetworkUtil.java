/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

public class NetworkUtil {

    private static String LOG_TAG = "NetWorkHelper";

    public static Uri uri = Uri.parse("content://telephony/carriers");

    /**
     * 判断是否有网络连接
     */
    // public static boolean isNetworkAvailable(Context context) {
    // ConnectivityManager connectivity = (ConnectivityManager) context
    // .getSystemService(Context.CONNECTIVITY_SERVICE);
    //
    // if (connectivity == null) {
    // Log.w(LOG_TAG, "couldn't get connectivity manager");
    // } else {
    // NetworkInfo[] info = connectivity.getAllNetworkInfo();
    // if (info != null) {
    // for (int i = 0; i < info.length; i++) {
    // if (info[i].isAvailable()) {
    // Log.d(LOG_TAG, "network is available");
    // return true;
    // }
    // }
    // }
    // }
    // Log.d(LOG_TAG, "network is not available");
    // return false;
    // }

    /**
     * 检查网络是否可用
     *
     * @param context 应用程序的上下文对象
     * @return
     */
    public static boolean networkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static boolean checkNetState(Context context) {
        boolean netstate = false;
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        netstate = true;
                        break;
                    }
                }
            }
        }
        return netstate;
    }

    /**
     * 判断网络是否为漫游
     */
//	public static boolean isNetworkRoaming(Context context) {
//		ConnectivityManager connectivity = (ConnectivityManager) context
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		if (connectivity == null) {
//			Log.w(LOG_TAG, "couldn't get connectivity manager");
//		} else {
//			NetworkInfo info = connectivity.getActiveNetworkInfo();
//			if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
//				TelephonyManager tm = (TelephonyManager) context
//						.getSystemService(Context.TELEPHONY_SERVICE);
//				if (tm != null && tm.isNetworkRoaming()) {
//					Log.d(LOG_TAG, "network is roaming");
//					return true;
//				} else {
//					Log.d(LOG_TAG, "network is not roaming");
//				}
//			} else {
//				Log.d(LOG_TAG, "not using mobile network");
//			}
//		}
//		return false;
//	}

    /**
     * 判断MOBILE网络是否可用
     *
     * @param context
     * @return
     * @throws Exception
     */
//	public static boolean isMobileDataEnable(Context context) throws Exception {
//		ConnectivityManager connectivityManager = (ConnectivityManager) context
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		boolean isMobileDataEnable = false;
//
//		isMobileDataEnable = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
//				.isConnectedOrConnecting();
//
//		return isMobileDataEnable;
//	}

    /**
     * 判断wifi 是否可用
     *
     * @param context
     * @return
     * @throws Exception
     */
//	public static boolean isWifiDataEnable(Context context) throws Exception {
//		ConnectivityManager connectivityManager = (ConnectivityManager) context
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		boolean isWifiDataEnable = false;
//		isWifiDataEnable = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
//				.isConnectedOrConnecting();
//		return isWifiDataEnable;
//	}

//	public static boolean checkNetworkConnection(Context context) {
//		ConnectivityManager connectivity = (ConnectivityManager) context
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//		return connectivity.getActiveNetworkInfo() != null;
//	}

    /**
     * 判断当前使用的是否wap连接
     *
     * @param context
     * @return
     */
//	public static boolean isWapConnecting(Context context) {
//
//		ConnectivityManager manager = (ConnectivityManager) context
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo netWrokInfo = manager.getActiveNetworkInfo();
//		if (netWrokInfo != null && netWrokInfo.isAvailable()
//				&& netWrokInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
//			// String proxyHost = android.net.Proxy.getDefaultHost();
//			// return proxyHost;
//			String wapType = netWrokInfo.getExtraInfo();
//			if (wapType != null) {
//				if (wapType.toLowerCase().indexOf("wap") >= 0) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}

    /**
     * 判断当前使用的是否wifi连接
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnecting(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netWorkInfo = manager.getActiveNetworkInfo();
        if (netWorkInfo != null && netWorkInfo.isAvailable()
                && netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static final int CONNECTIONTYPE_NONE = 0;
    public static final int CONNECTIONTYPE_MOBILE = 1;
    public static final int CONNECTIONTYPE_WIFI = 2;

    public static int getConnectionType(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netWrokInfo = connectivity.getActiveNetworkInfo();
        return getConnectionType(netWrokInfo);
    }

    public static int getConnectionType(NetworkInfo netWrokInfo) {
        if (netWrokInfo == null || !netWrokInfo.isAvailable()) {
            return CONNECTIONTYPE_NONE;
        }
        if (netWrokInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return CONNECTIONTYPE_WIFI;
        }
        return CONNECTIONTYPE_MOBILE;
    }

    /**
     * 获取当前网络的连接方式
     *
     * @return CMNET、WIFI等，若无法获取则返回UNKNOW
     */
    public static String getNetConnectionType(Context context) {
        String typeName = "UNKNOW";
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null)
            typeName = info.getTypeName(); // cmwap | cmnet | wifi | uniwap 等等
        return typeName;
    }

//    public static void senNotification(String toUid, String msgType, String message, String ext) {
//        if (toUid != null && !toUid.equals("")) {
//            NotificationRequest request = new NotificationRequest("sendnotification", toUid, msgType, message, ext);
//            request.send(new BaseDataModel.RequestCallback<String>() {
//                @Override
//                public void onSuccess(String pojo) {
//                    Log.i("info", "====通知发送完成!");
//                }
//
//                @Override
//                public void onFailure(String msg) {
//
//                }
//            });
//        }
//    }
}

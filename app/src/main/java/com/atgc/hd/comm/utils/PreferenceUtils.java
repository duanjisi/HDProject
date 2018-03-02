/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * <p>描述： 文件保存数据类
 * <p>作者： duanjisi 2018/1/11
 */
public class PreferenceUtils {


    /**
     * 清空应用中的文件存储
     *
     * @param context
     */
    public static void reset(final Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear()
                .commit();
    }

    /**
     * 获取String文件信息
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String getString(final Context context, String key,
                                   String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, defValue);
    }

    /**
     * 存储String类型的数据到文件当中
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putString(final Context context, String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString(key, value).commit();
    }

    /**
     * 获取int文件信息
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int getInt(final Context context, String key, int defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(
                key, defValue);
    }

    /**
     * 存储int类型数据到文件当中
     *
     * @param context
     * @param key
     * @param value
     */
    public static void putInt(final Context context, String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putInt(key, value).commit();
    }

    public static void putBoolean(final Context context, String key,
                                  Boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(key, value).commit();
    }


    public static boolean getBoolean(final Context context, String key,
                                     boolean defValue) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(key, defValue);
    }

}

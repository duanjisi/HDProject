package com.atgc.hd.comm.utils;

import android.util.DisplayMetrics;

import com.atgc.hd.HDApplication;

import java.lang.reflect.Field;

/**
 * <p>描述：
 * <p>作者： liangguokui 2018/1/17
 */
public class SysManager {

    private static int STATUSBAT_HEIGHT = -1;

    private SysManager() {

    }

    /**
     * 通过反射的方式获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        if (STATUSBAT_HEIGHT == -1) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                STATUSBAT_HEIGHT = HDApplication.applicationContext().getResources().getDimensionPixelSize(x);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        return STATUSBAT_HEIGHT;
    }

    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics display = HDApplication.applicationContext().getResources().getDisplayMetrics();
        return display;
    }

}

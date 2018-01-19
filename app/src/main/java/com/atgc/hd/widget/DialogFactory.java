/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.widget;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

/**
 * <p>描述：根据不同的版本创建对话框
 * <p>作者：duanjisi 2018年 01月 18日
 */
public class DialogFactory {
    /**
     * 根据不同的android版本创建不同风格的对话框builder
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static AlertDialog.Builder createAlertDialogBuilder(Context context) {
        AlertDialog.Builder builder = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        } else {
            builder = new AlertDialog.Builder(context);
        }
        return builder;
    }

    /**
     * 根据不同的android版本创建不同风格的ProgressDialog
     *
     * @param context
     * @return
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog dialog = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            dialog = new ProgressDialog(context, ProgressDialog.STYLE_SPINNER);
        } else {
            dialog = new ProgressDialog(context);
        }

        return dialog;
    }
}
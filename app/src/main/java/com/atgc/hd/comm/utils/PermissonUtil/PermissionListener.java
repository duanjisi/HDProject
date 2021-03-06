/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.utils.PermissonUtil;

/**
 * Created by GMARUnity on 2017/2/16.
 */
public interface PermissionListener {

    void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults);

    void onRequestPermissionSuccess();

    void onRequestPermissionError();

}

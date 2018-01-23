/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.adapter;

import android.view.View;

import com.atgc.hd.entity.UploadEntity;

/**
 * <p>描述：更新进度
 * <p>作者：duanjisi 2018年 01月 22日
 */

public interface UpdateCallback {
    void freshProgress(View view, UploadEntity entity, int position);
}

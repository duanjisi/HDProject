/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.entity;

import android.content.Context;

import com.atgc.hd.comm.net.http.MyTask;

/**
 * <p>描述：上传文件实体(图片，视频)
 * <p>作者：duanjisi 2018年 01月 22日
 */

public class UploadEntity {
    public int id;
    public int progress = 0;       //下载进度
    private int type;
    private String localPath = "";
    private String url = "";
    boolean Downloaded = false;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isDownloaded() {
        return Downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        Downloaded = downloaded;
    }
}

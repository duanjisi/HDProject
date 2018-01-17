/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.entity;

import java.io.Serializable;

/**
 * <p>描述：网络数据类的基类
 * <p>作者：duanjisi 2018年 01月 16日
 */
public abstract class BaseData implements Serializable {

    protected static final int STATUS_OK = 0;
    protected static final int STATUS_ERROR = -1;
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private transient int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

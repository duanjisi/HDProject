/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.entity;

/**
 * <p>描述：行为类型实体
 * <p>作者：duanjisi 2018年 01月 16日
 */

public class ActionEntity {
    private String action = "";
    private Object data = null;

    public ActionEntity(String action) {
        this.action = action;
    }

    public ActionEntity(String action, Object data) {
        this.action = action;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public String getAction() {
        return action;
    }
}

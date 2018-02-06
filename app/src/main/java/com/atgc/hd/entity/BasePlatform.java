/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.entity;

import java.util.ArrayList;

/**
 * <p>描述：
 * <p>作者：duanjisi 2018年 02月 05日
 */

public class BasePlatform {
    private ArrayList<PatInfo> Data = new ArrayList<>();

    public ArrayList<PatInfo> getData() {
        return Data;
    }

    public void setData(ArrayList<PatInfo> data) {
        Data = data;
    }
}

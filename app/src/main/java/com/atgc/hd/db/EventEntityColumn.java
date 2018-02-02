/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.db;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>描述： 应急事件实体列表
 * <p>作者：duanjisi 2018年 02月 01日
 */

public class EventEntityColumn extends DatabaseColumn {
    public static final String TABLE_NAME = "eventTable";
    public static final String URLS = "urls";
    public static final String PLACE = "place";
    public static final String DESCRIPTION = "description";
    public static final String TIME = "time";
    private static final Map<String, String> mColumnMap = new HashMap<String, String>();

    static {
        mColumnMap.put(_ID, "integer primary key autoincrement");
        mColumnMap.put(URLS, "text");
        mColumnMap.put(PLACE, "text");
        mColumnMap.put(DESCRIPTION, "text");
        mColumnMap.put(TIME, "text");
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    protected Map<String, String> getTableMap() {
        return mColumnMap;
    }
}

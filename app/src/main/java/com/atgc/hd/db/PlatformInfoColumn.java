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

public class PlatformInfoColumn extends DatabaseColumn {
    public static final String TABLE_NAME = "platformTable";

    public static final String MESSAGE_ID = "messageID";
    public static final String MESSAGE_CONTENT = "messageContent";
    public static final String DEVICE_ID = "deviceID";
    public static final String TYPE = "type";
    public static final String EVENT_ADDR = "eventAddr";
    public static final String PIC_URL = "picUrl";
    public static final String SEND_TIME = "sendTime";
    private static final Map<String, String> mColumnMap = new HashMap<String, String>();

    static {
        mColumnMap.put(_ID, "integer primary key autoincrement");
        mColumnMap.put(DEVICE_ID, "text");
        mColumnMap.put(MESSAGE_CONTENT, "text");
        mColumnMap.put(MESSAGE_ID, "text");
        mColumnMap.put(TYPE, "text");
        mColumnMap.put(SEND_TIME, "text");
        mColumnMap.put(EVENT_ADDR, "text");
        mColumnMap.put(PIC_URL, "text");
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

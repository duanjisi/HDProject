/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.atgc.hd.HDApplication;
import com.atgc.hd.db.DBHelper;
import com.atgc.hd.db.PlatformInfoColumn;
import com.atgc.hd.db.PlatformInfoColumn;
import com.atgc.hd.db.helper.ColumnHelper;
import com.atgc.hd.entity.EventEntity;
import com.atgc.hd.entity.PatInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>描述：事件dao接口
 * <p>作者：duanjisi 2018年 02月 01日
 */

public class PlatformInfoDao extends ColumnHelper<PatInfo> {
    private static PlatformInfoDao eventDao;
    private Context mContext;

    public PlatformInfoDao() {
        mContext = HDApplication.getInstance().getApplicationContext();
    }

    public static PlatformInfoDao getInstance() {
        if (eventDao == null) {
            synchronized (PlatformInfoDao.class) {
                if (eventDao == null) {
                    eventDao = new PlatformInfoDao();
                }
            }
        }
        return eventDao;
    }

    @Override
    public void save(List<PatInfo> list) {

    }

    @Override
    public void save(PatInfo entity) {
        String[] args = new String[]{entity.getMessageID()};
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                getSelectSql(PlatformInfoColumn.TABLE_NAME, new String[]{PlatformInfoColumn.MESSAGE_ID}), args);
        if (exist(c)) {
//            c.moveToFirst();
//            this.delete(entity.getUser_name());
            this.update(entity);
        } else {
            DBHelper.getInstance(mContext).insert(PlatformInfoColumn.TABLE_NAME, getValues(entity));
        }
        c.close();
    }

    @Override
    public PatInfo query(int id) {
        return null;
    }

    @Override
    public List<PatInfo> query() {
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                "SELECT * FROM " + PlatformInfoColumn.TABLE_NAME, null);
        List<PatInfo> bos = new ArrayList<PatInfo>();
        if (exist(c)) {
            c.moveToLast();
            do {
                bos.add(getBean(c));
            } while (c.moveToPrevious());
        }
        c.close();
        return bos;
    }

    @Override
    public void delete(int id) {

    }

    @Override
    public void delete(String str) {

    }

    @Override
    public void update(PatInfo entity) {

    }

    @Override
    protected ContentValues getValues(PatInfo bean) {
        ContentValues values = new ContentValues();
        values.put(PlatformInfoColumn.DEVICE_ID, bean.getDeviceID());
        values.put(PlatformInfoColumn.MESSAGE_CONTENT, bean.getMessageContent());
        values.put(PlatformInfoColumn.MESSAGE_ID, bean.getMessageID());
        values.put(PlatformInfoColumn.SEND_TIME, bean.getSendTime());
        values.put(PlatformInfoColumn.TYPE, bean.getType());
        return values;
    }

    @Override
    protected PatInfo getBean(Cursor c) {
        PatInfo entity = new PatInfo();
        entity.setDeviceID(getString(c, PlatformInfoColumn.DEVICE_ID));
        entity.setMessageContent(getString(c, PlatformInfoColumn.MESSAGE_CONTENT));
        entity.setMessageID(getString(c, PlatformInfoColumn.MESSAGE_ID));
        entity.setSendTime(getString(c, PlatformInfoColumn.SEND_TIME));
        entity.setType(getString(c, PlatformInfoColumn.TYPE));
        return entity;
    }

    /**
     * 删除表内所有数据
     */
    public void deleteAllDatas() {
        DBHelper.getInstance(mContext).delete(PlatformInfoColumn.TABLE_NAME, null, null);
    }
}

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
import com.atgc.hd.db.EventEntityColumn;
import com.atgc.hd.db.helper.ColumnHelper;
import com.atgc.hd.entity.EventEntity;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>描述：事件dao接口
 * <p>作者：duanjisi 2018年 02月 01日
 */

public class EventDao extends ColumnHelper<EventEntity> {
    private static EventDao eventDao;
    private Context mContext;

    public EventDao() {
        mContext = HDApplication.getInstance().getApplicationContext();
    }

    public static EventDao getInstance() {
        if (eventDao == null) {
            synchronized (EventDao.class) {
                if (eventDao == null) {
                    eventDao = new EventDao();
                }
            }
        }
        return eventDao;
    }

    @Override
    public void save(List<EventEntity> list) {

    }

    @Override
    public void save(EventEntity entity) {
        String[] args = new String[]{entity.getTime()};
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                getSelectSql(EventEntityColumn.TABLE_NAME, new String[]{EventEntityColumn.TIME}), args);
        if (exist(c)) {
//            c.moveToFirst();
//            this.delete(entity.getUser_name());
            this.update(entity);
        } else {
            DBHelper.getInstance(mContext).insert(EventEntityColumn.TABLE_NAME, getValues(entity));
        }
        c.close();
    }

    @Override
    public EventEntity query(int id) {
        return null;
    }

    @Override
    public List<EventEntity> query() {
        Cursor c = DBHelper.getInstance(mContext).rawQuery(
                "SELECT * FROM " + EventEntityColumn.TABLE_NAME, null);
        List<EventEntity> bos = new ArrayList<EventEntity>();
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
    public void update(EventEntity entity) {

    }

    @Override
    protected ContentValues getValues(EventEntity bean) {
        ContentValues values = new ContentValues();
        values.put(EventEntityColumn.URLS, bean.getUrls());
        values.put(EventEntityColumn.PLACE, bean.getPlace());
        values.put(EventEntityColumn.DESCRIPTION, bean.getDescription());
        values.put(EventEntityColumn.TIME, bean.getTime());
        return values;
    }

    @Override
    protected EventEntity getBean(Cursor c) {
        EventEntity entity = new EventEntity();
        entity.setDescription(getString(c, EventEntityColumn.DESCRIPTION));
        entity.setPlace(getString(c, EventEntityColumn.PLACE));
        entity.setUrls(getString(c, EventEntityColumn.URLS));
        entity.setTime(getString(c, EventEntityColumn.TIME));
        return entity;
    }

    /**
     * 删除表内所有数据
     */
    public void deleteAllDatas() {
        DBHelper.getInstance(mContext).delete(EventEntityColumn.TABLE_NAME, null, null);
    }
}

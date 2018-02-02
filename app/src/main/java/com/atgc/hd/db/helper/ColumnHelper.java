
package com.atgc.hd.db.helper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public abstract class ColumnHelper<T> implements IDatabase<T> {
    /**
     * 判断数据是否存在
     *
     * @param
     * @return
     */
    protected boolean exist(Cursor c, Context context) {
        boolean flag = c.getCount() > 0;
        //TODO
//        c.close();
        return flag;
    }

    protected synchronized boolean exist(Cursor c) {
        boolean flag = c.getCount() > 0;
        return flag;
    }

    protected synchronized boolean isCursorEmptyOrNotPrepared(Cursor cursor) {
        if (cursor == null)
            return true;

        if (cursor.isClosed())
            return true;

        if (cursor.getCount() == 0) // HERE IT CRASHES
            return true;

        return false;
    }

    protected int getInt(Cursor c, String columnName) {
        return c.getInt(c.getColumnIndex(columnName));
    }

    protected String getString(Cursor c, String columnName) {
        return c.getString(c.getColumnIndex(columnName));
    }

    protected long getLong(Cursor c, String columnName) {
        return c.getLong(c.getColumnIndex(columnName));
    }

    protected static String getSelectSql(String tableName, String[] args) {
        StringBuilder builder = new StringBuilder("SELECT * FROM ");
        builder.append(tableName).append(" WHERE ");
        int length = args.length;
        for (int i = 0; i < length; i++) {
            builder.append(args[i]).append(" = ?");
            if (i != length - 1) {
                builder.append(" AND ");
            }
        }
        return builder.toString();
    }

    protected static String getSelectSql1(String tableName
            , String colum, int count) {//查询某个字段多个值数据
        StringBuilder builder = new StringBuilder("SELECT * FROM ");
        builder.append(tableName).append(" WHERE ");

        int length = count;
        for (int i = 0; i < length; i++) {
            builder.append(colum).append(" = ?");
            if (i != length - 1) {
                builder.append(" OR ");
            }
        }
        return builder.toString();
    }

//    protected static String getSql(String tableName) {//查询某个字段多个值数据
//        StringBuilder builder = new StringBuilder("SELECT * FROM ");
//        builder.append(tableName).append(" WHERE ");
//        builder.append("user_id = ").append("?");
//        builder.append(" and ");
//        builder.append(VideoCacheColumn.CACHE_STATUS);
//        builder.append(" in (?,?,?)");
//        return builder.toString();
//    }


    protected static String getWhereStr(String key) {
        return key + " = ?";
    }

    protected abstract ContentValues getValues(T bean);

    protected abstract T getBean(Cursor c);
}

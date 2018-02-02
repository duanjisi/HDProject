/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "train.db";
    public static final int DB_VERSION = 9;

    private SQLiteDatabase db;

    private static DBHelper mdbHelper;

    public static synchronized DBHelper getInstance(Context context) {
        if (mdbHelper == null) {
            mdbHelper = new DBHelper(context);
        }
        return mdbHelper;
    }


    private DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    private DBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        operateTable(db, "");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == newVersion) {
            return;
        }
        // if (oldVersion <= 3 && newVersion >= 4) { // 群信息中增加群创建者字段
        // db.execSQL("ALTER TABLE " + GroupsColumn.TABLE_NAME + " ADD COLUMN "
        // + GroupsColumn.GROUP_CREATOR);
        // return;
        // }
        operateTable(db, "DROP TABLE IF EXISTS ");
        onCreate(db);
    }

    public void operateTable(SQLiteDatabase db, String actionString) {
        Class<DatabaseColumn>[] columnsClasses = DatabaseColumn.getSubClasses();
        DatabaseColumn columns = null;

        for (int i = 0; i < columnsClasses.length; i++) {
            try {
                columns = columnsClasses[i].newInstance();
                if ("".equals(actionString) || actionString == null) {
                    db.execSQL(columns.getTableCreateor());
                } else {
                    db.execSQL(actionString + columns.getTableName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public long insert(String Table_Name, ContentValues values) {
//        if (db == null) {
//            db = getWritableDatabase();
//        }
        synchronized (this) {
            if (db == null) {
                db = getWritableDatabase();
            } else {
                if (!db.isOpen()) {
                    db = getWritableDatabase();
                }
            }
            return db.insert(Table_Name, null, values);
        }
    }

    public long replace(String Table_Name, ContentValues values) {
        if (db == null) {
            db = getWritableDatabase();
        }
        return db.replace(Table_Name, null, values);
    }

    /**
     * @param Table_Name
     * @param id
     * @return 影响行数
     */
    public int delete(String Table_Name, int id) {
//        if (db == null)
//            db = getWritableDatabase();

        synchronized (this) {
            if (db == null) {
                db = getWritableDatabase();
            } else {
                if (!db.isOpen()) {
                    db = getWritableDatabase();
                }
            }
            return db.delete(Table_Name, BaseColumns._ID + "=?", new String[]{String.valueOf(id)});
        }
    }

    public int delete(String Table_Name, String whereClause, String[] whereArgs) {
//        if (db == null)
//            db = getWritableDatabase();
        synchronized (this) {
            if (db == null) {
                db = getWritableDatabase();
            } else {
                if (!db.isOpen()) {
                    db = getWritableDatabase();
                }
            }
            return db.delete(Table_Name, whereClause, whereArgs);
        }
    }

    /**
     * @param Table_Name
     * @param values
     * @param WhereClause
     * @param whereArgs
     * @return 影响行数
     */

    public int update(String Table_Name, ContentValues values, String WhereClause,
                      String[] whereArgs) {
//        if (db == null) {
//            db = getWritableDatabase();
//        }
        synchronized (this) {
            if (db == null) {
                db = getWritableDatabase();
            } else {
                if (!db.isOpen()) {
                    db = getWritableDatabase();
                }
            }
            return db.update(Table_Name, values, WhereClause, whereArgs);
        }
    }

    public Cursor query(String Table_Name, String[] columns, String whereStr, String[] whereArgs) {
//        if (db == null) {
//            db = getReadableDatabase();
//        }
        synchronized (this) {
            if (db == null) {
                db = getReadableDatabase();
            } else {
                if (!db.isOpen()) {
                    db = getReadableDatabase();
                }
            }
            return db.query(Table_Name, columns, whereStr, whereArgs, null, null, null);
        }
    }

    public Cursor rawQuery(String sql, String[] args) {
//        if (db == null) {
//            db = getReadableDatabase();
//        }
        synchronized (this) {
            if (db == null) {
                db = getReadableDatabase();
            } else {
                if (!db.isOpen()) {
                    db = getReadableDatabase();
                }
            }
            return db.rawQuery(sql, args);
        }
    }

    public void ExecSQL(String sql) {
        if (db == null) {
            db = getWritableDatabase();
        }
        db.execSQL(sql);
    }

    public void closeDb() {
        if (db != null && db.isOpen()) {
            db.close();
            db = null;
        }
    }
}

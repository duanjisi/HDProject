package com.atgc.hd.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.atgc.hd.entity.MessageItem;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>描述：网络数据类的基类
 * <p>作者：duanjisi 2018年 01月 16日
 */
public class MessageDB {
    public static final String MSG_DBNAME = "message.db";
    private SQLiteDatabase db;

    public MessageDB(Context context) {
        db = context.openOrCreateDatabase(MSG_DBNAME, Context.MODE_PRIVATE,
                null);
    }

    public MessageItem saveMsg(String id, MessageItem entity) {
        db.execSQL("CREATE table IF NOT EXISTS _"
                + id
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,messagetype INTEGER,name TEXT, img TEXT,date TEXT,isCome TEXT,message TEXT,isNew TEXT,voiceTime INTEGER,temp TEXT,nick TEXT,userid TEXT,avatar TEXT)");
        int isCome = 0;
        if (entity.isComMeg()) {// 如果是收到的消息，保存在数据库的值为1
            isCome = 1;
        }
        db.execSQL(
                "insert into _"
                        + id
                        + " (messagetype,name,img,date,isCome,message,isNew,voiceTime,temp,nick,userid,avatar) values(?,?,?,?,?,?,?,?,?,?,?,?)",
                new Object[]{entity.getMsgType(), entity.getName(),
                        entity.getHeadImg(), entity.getDate(), isCome,
                        entity.getMessage(), entity.getIsNew(),
                        entity.getVoiceTime(), entity.getTemp(), entity.getNick(),
                        entity.getUserid(), entity.getAvatar()});

//        Cursor cur = db.rawQuery("select last_insert_id() from " + "_" + id, null);
        Cursor cur = db.rawQuery("SELECT * from _" + id
                + " ORDER BY _id DESC LIMIT " + 1, null);
        int msgid = -1;
        if (cur.moveToFirst()) {
            msgid = cur.getInt(cur.getColumnIndex("_id"));
        }
        entity.setMsgId("" + msgid);
        cur.close();
        return entity;
    }


//    private String getMd5Str(MessageItem item) {
//        String md5 = Utils.getMd5(item.toString());
//        Log.i("info", "==============md5:" + md5);
//        return md5;
//    }

    public List<MessageItem> getMsg(String id, int pager) {
        List<MessageItem> list = new LinkedList<MessageItem>();
        int num = 10 * (pager + 1);// 本来是准备做滚动到顶端自动加载数据
//        db.execSQL("CREATE table IF NOT EXISTS _"
//                + id
//                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,messagetype INTEGER,name TEXT, img TEXT,date TEXT,isCome TEXT,message TEXT,isNew TEXT,voiceTime INTEGER,temp TEXT,nick TEXT,userid TEXT,avatar TEXT)");
        db.execSQL("CREATE table IF NOT EXISTS _"
                + id
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,messagetype INTEGER,name TEXT, img TEXT,date TEXT,isCome TEXT,message TEXT,isNew TEXT,voiceTime INTEGER,temp TEXT,nick TEXT,userid TEXT,avatar TEXT)");
        Cursor c = db.rawQuery("SELECT * from _" + id
                + " ORDER BY _id DESC LIMIT " + num, null);
        while (c.moveToNext()) {
            int _id = c.getInt(c.getColumnIndex("_id"));
            String name = c.getString(c.getColumnIndex("name"));
            int img = c.getInt(c.getColumnIndex("img"));
            long date = c.getLong(c.getColumnIndex("date"));
            int isCome = c.getInt(c.getColumnIndex("isCome"));
            String message = c.getString(c.getColumnIndex("message"));
            int isNew = c.getInt(c.getColumnIndex("isNew"));
            int msgType = c.getInt(c.getColumnIndex("messagetype"));
            int voiceTime = c.getInt(c.getColumnIndex("voiceTime"));
            String temp = c.getString(c.getColumnIndex("temp"));
            String nick = c.getString(c.getColumnIndex("nick"));
            String userid = c.getString(c.getColumnIndex("userid"));
            String avatar = c.getString(c.getColumnIndex("avatar"));
            boolean isComMsg = false;
            if (isCome == 1) {
                isComMsg = true;
            }
            MessageItem entity = new MessageItem(msgType, name, date, message,
                    img, isComMsg, isNew, voiceTime, temp, nick, userid, avatar);
            entity.setMsgId("" + _id);
            list.add(entity);
        }
        c.close();
        Collections.reverse(list);// 前后反转一下消息记录
        return list;
    }

    public int getNewCount(String id) {
        db.execSQL("CREATE table IF NOT EXISTS _"
                + id
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, img TEXT,date TEXT,isCome TEXT,message TEXT,isNew TEXT,voiceTime INTEGER)");
        Cursor c = db.rawQuery("SELECT isNew from _" + id + " where isNew=1",
                null);
        int count = c.getCount();
        // L.i("new message num = " + count);
        c.close();
        return count;
    }

    public void clearNewCount(String id) {
        db.execSQL("CREATE table IF NOT EXISTS _"
                + id
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, img TEXT,date TEXT,isCome TEXT,message TEXT,isNew TEXT,voiceTime INTEGER)");
        db.execSQL("update _" + id + " set isNew=0 where isNew=1");
    }

    public void clearMsgDatas(String id) {
//        db.execSQL("CREATE table IF NOT EXISTS _"
//                + id
//                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,name TEXT, img TEXT,date TEXT,isCome TEXT,message TEXT,isNew TEXT,voiceTime INTEGER)");
        db.execSQL("CREATE table IF NOT EXISTS _"
                + id
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,messagetype INTEGER,name TEXT, img TEXT,date TEXT,isCome TEXT,message TEXT,isNew TEXT,voiceTime INTEGER,temp TEXT,nick TEXT,userid TEXT,avatar TEXT)");
        db.execSQL("delete from " + "_" + id);
    }

    public void clearMsgItem(MessageItem item, String id) {
//        db.execSQL("CREATE table IF NOT EXISTS _"
//                + id
//                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,messagetype INTEGER,name TEXT, img TEXT,date TEXT,isCome TEXT,message TEXT,isNew TEXT,voiceTime INTEGER,temp TEXT,nick TEXT,userid TEXT,avatar TEXT)");
        db.execSQL("CREATE table IF NOT EXISTS _"
                + id
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,messagetype INTEGER,name TEXT, img TEXT,date TEXT,isCome TEXT,message TEXT,isNew TEXT,voiceTime INTEGER,temp TEXT,nick TEXT,userid TEXT,avatar TEXT)");
        String sql = "delete from " + "_" + id + " where " + "_id=?";
        Log.i("info", "==========sql:" + sql);
        db.execSQL(sql, new String[]{item.getMsgId()});
    }

    private String getWhere(MessageItem item) {
        String where = "name=?" + " and " +
                "messagetype=?" + " and " +
                "img=?" + " and " +
                "date=?" + " and " +
                "isCome=?" + " and " +
                "message=?" + " and " +
                "nick=?" + " and " +
                "userid=?" + " and " +
                "avatar=?";
        return where;
    }

    public void close() {
        if (db != null)
            db.close();
    }
}

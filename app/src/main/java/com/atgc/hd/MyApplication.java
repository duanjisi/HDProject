package com.atgc.hd;

import android.app.Application;

import com.atgc.hd.entity.Header;

/**
 * 全局Application
 * Created by duanjisi on 2018/1/15.
 */

public class MyApplication extends Application {
    private static MyApplication mApplication;
    private Header header = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
    }

    public synchronized static MyApplication getInstance() {
        return mApplication;
    }

    /**
     * 获取协议头部信息
     *
     * @return
     */
    public Header getHeader() {
        if (header == null) {
            header = new Header();
            header.setVersion("HDXM");
            header.setSrcID("10012017f6d0101be5ed");
            header.setDestID("1004201658FCDBD8341E");
            header.setRequest("1");
            header.setHold("0");
            header.setPackNo("172");
            header.setCrc("42027");
        }
        return header;
    }
}

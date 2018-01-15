package com.atgc.hd;

import android.app.Application;

import com.atgc.hd.comm.crash.CrashHandler;
import com.atgc.hd.comm.utils.FileUtil;
import com.atgc.hd.entity.Header;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.adapter.AndroidLogAdapter;
import com.orhanobut.logger.adapter.DiskLogAdapter;

/**
 * <p>描述：
 * <p>作者： liangguokui 2018/1/12
 */
public class AppApplication extends Application {
    private static AppApplication mApplication;
    private Header header = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        initLog();
        initCrashHandler();
    }

    public synchronized static AppApplication getInstance() {
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
            header.setSrcID("10012017020000000000");
            header.setDestID("1004201658FCDBD8341E");
            header.setRequest("1");
            header.setHold("0");
            header.setPackNo("172");
            header.setCrc("42027");
        }
        return header;
    }

    private void initLog() {
        Logger.addLogAdapter(new AndroidLogAdapter());
        Logger.addLogAdapter(new DiskLogAdapter(FileUtil.LOG_PATH));
    }

    private void initCrashHandler() {
        CrashHandler crashHandler = CrashHandler.instance();
        crashHandler.init(getApplicationContext());
    }
}

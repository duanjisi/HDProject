package com.atgc.hd;

import android.app.Application;

import com.atgc.hd.comm.crash.CrashHandler;
import com.atgc.hd.comm.utils.FileUtil;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.adapter.AndroidLogAdapter;
import com.orhanobut.logger.adapter.DiskLogAdapter;

/**
 * <p>描述：
 * <p>作者： liangguokui 2018/1/12
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initLog();
        initCrashHandler();
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

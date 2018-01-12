package com.atgc.hd.comm.utils;

import android.os.Environment;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * <p>描述： 文件操作工具类
 * <p>作者： liangguokui 2018/1/12
 */
public class FileUtil {

    public static final String SD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String ROOT_FOLDER = "hdpatrol";

    public static final String ROOT_PATH = SD_PATH + File.separatorChar + ROOT_FOLDER;
    public static final String LOG_PATH = File.separatorChar + ROOT_PATH + File.separatorChar + "log";
    public static final String CRASH_LOG_ROOT = LOG_PATH + File.separatorChar + "crashlog" + File.separatorChar;

    public static final void closeQuietly(Closeable closeable) {
        if (closeable == null) {
            return;
        } else {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

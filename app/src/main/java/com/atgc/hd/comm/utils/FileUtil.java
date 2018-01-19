package com.atgc.hd.comm.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import static com.nostra13.universalimageloader.utils.StorageUtils.getCacheDirectory;

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


    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";

    public static File createTmpFile(Context context) throws IOException {
        File dir = null;
        if (TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
            dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
            if (!dir.exists()) {
                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera");
                if (!dir.exists()) {
                    dir = getCacheDirectory(context, true);
                }
            }
        } else {
            dir = getCacheDirectory(context, true);
        }
        return File.createTempFile(JPEG_FILE_PREFIX, JPEG_FILE_SUFFIX, dir);
    }

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


    public static void close(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

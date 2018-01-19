package com.atgc.hd.comm.crash;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.atgc.hd.MainActivity;
import com.atgc.hd.base.ActivityQueue;
import com.atgc.hd.comm.utils.DateUtil;
import com.atgc.hd.comm.utils.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>描述：未知异常捕捉、记录
 * <p>作者：liangguokui 2018/1/12
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static final String TAG = "CrashHandler";

    private static CrashHandler crashHandler;

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    private static Date curDate = new Date(System.currentTimeMillis());
    private static String str = formatter.format(curDate);

    private boolean mIsRestartApp = false;
    private long mRestartTime = 500;

    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    //程序的Context对象
    private Context mContext;

    //用来存储设备信息和异常信息
    private List<String> deviceInfos = new ArrayList<>();

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        if (!handleException(ex) && mDefaultHandler != null) {
            //如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
            return;
        }

        sleep(3 * 1000);

        // 需要重启app
        if (mIsRestartApp) {
            Intent intent = new Intent(mContext.getApplicationContext(), MainActivity.class);
            AlarmManager mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            //重启应用，得使用PendingIntent
            PendingIntent restartIntent = PendingIntent.getActivity(
                    mContext.getApplicationContext(),
                    0,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            // 重启应用
            mAlarmManager.set(AlarmManager.RTC,
                    System.currentTimeMillis() + mRestartTime,
                    restartIntent);
        }
        // 不需要重启app
        else {
            // 移除所有的activity
            ActivityQueue.intance().removeAllActivity();
            //退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }

    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Log.e(TAG, "error : ", e);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        if (mIsRestartApp) {
            sendExceptionTips("出现异常，即将重启...");
        } else {
            sendExceptionTips("出现异常，即将退出...");
        }
        //收集设备参数信息
        collectDeviceInfo(mContext);
        //保存日志文件
        saveCrashInfo2File(ex);

        return true;
    }

    private void sendExceptionTips(final String tips) {
        //使用Toast来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();//准备发消息的MessageQueue
                Toast.makeText(mContext, tips, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 收集设备参数信息
     *
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);

            deviceInfos.add("手机品牌: " + Build.BRAND);
            deviceInfos.add("手机型号: " + Build.MODEL);

            deviceInfos.add("系统版本: " + Build.VERSION.SDK_INT);

            deviceInfos.add("软件版本名: " + pi.versionName);
            deviceInfos.add("软件版本号: " + pi.versionCode);

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称, 便于将文件传送到服务器
     */
    private String saveCrashInfo2File(Throwable ex) {

        StringBuilder sb = new StringBuilder();

        sb.append(DateUtil.currentTime());

        for (String deviceInfo : deviceInfos) {
            sb.append("\n");
            sb.append(deviceInfo);
        }

        sb.append("\n异常信息：\n");
        String crashlog = getExceptionMsg(ex);
        sb.append(crashlog);
        sb.append("==============================\n\n");

        FileOutputStream fileOutputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedWriter bufferedWriter = null;
        try {

            String fileName = "crash-" + str + ".log";
//            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            String path = FileUtil.CRASH_LOG_ROOT;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            fileOutputStream = new FileOutputStream(path + fileName, true);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(sb.toString());

//            }
            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        } finally {
            FileUtil.closeQuietly(bufferedWriter);
            FileUtil.closeQuietly(outputStreamWriter);
            FileUtil.closeQuietly(fileOutputStream);
        }
        return null;
    }

    private String getExceptionMsg(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);

        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        String msg = writer.toString();

        FileUtil.closeQuietly(printWriter);
        FileUtil.closeQuietly(writer);

        return msg;
    }

    private CrashHandler() {
    }

    //单例
    public static CrashHandler instance() {
        if (crashHandler == null) {
            crashHandler = new CrashHandler();
        }
        return crashHandler;
    }
}


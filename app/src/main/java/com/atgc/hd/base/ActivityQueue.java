package com.atgc.hd.base;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>描述： 新启动的activity都需要加到该队列中，用于统一销毁
 * <p>作者： liangguokui 2018/1/19
 */
public class ActivityQueue {
    private static final ActivityQueue atyQueue = new ActivityQueue();

    private List<Activity> mActivityList;

    private ActivityQueue() {
        mActivityList = new ArrayList<>();
    }

    public static ActivityQueue intance() {
        return atyQueue;
    }

    /**
     * 添加单个Activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        // 为了避免重复添加，需要判断当前集合是否满足不存在该Activity
        if (!mActivityList.contains(activity)) {
            mActivityList.add(activity);
        }
    }

    /**
     * 销毁单个Activity
     *
     * @param activity
     */
    public void removeActivity(Activity activity) {
        // 判断当前集合是否存在该Activity
        if (mActivityList.contains(activity)) {
            mActivityList.remove(activity);
        }
    }

    /**
     * 销毁所有的Activity
     */
    public void removeAllActivity() {
        // 通过循环，把集合中的所有Activity销毁
        for (Activity activity : mActivityList) {
            if (activity != null) {
                activity.finish();
            }
        }
        //杀死该应用进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}

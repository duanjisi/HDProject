package com.atgc.hd.comm.clock;

import com.atgc.hd.comm.utils.DateUtil;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>描述： app内部的自定义时钟，用于与服务器时间同步（注：任何关于上传到服务器的时间，都必须从此类获取时间上传）
 * <p>作者： liangguokui 2018/1/11
 */
public class InnerClock {
    /**
     * 用于计时，单位秒
     */
    private int period;

    private Date serviceDate;

    private static InnerClock innerClock = new InnerClock();

    private InnerClock() {
    }

    public static InnerClock instance() {
        return innerClock;
    }

    /**
     * 初始化时钟
     * <p>注：一般只在登录成功时初始化，默认时间格式：yyyy-MM-dd HH:mm:ss
     * <p>可指定时间初始化格式：{@link #initClock(String, String)}
     *
     * @param time
     */
    public boolean initClock(String time) {
        return initClock(time, DateUtil.DATE_TIME_PATTERN);
    }

    /**
     * 指定时间格式初始化时钟
     * <p>注：一般只在登录成功时初始化
     * <p>默认时间初始化格式：{@link #initClock(String)}
     *
     * @param time
     */
    public boolean initClock(String time, String format) {
        serviceDate = DateUtil.dateParse(time, format);

        boolean initSuccess = serviceDate != null;
        if (initSuccess) {
            startTick();
            return true;
        } else {
            return false;
        }
    }

    private void startTick() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                period += 1;
            }
        };
        timer.schedule(timerTask, 0, 1 * 1000);
    }

    private synchronized long getTickCount() {
        long tickCount = period * 1000;
        return tickCount;
    }

    /**
     * 设置提醒时间
     *
     * @param alarmTime 提醒时间（格式：yyyy-MM-dd HH:mm:ss）
     * @param timerTask 时间到了之后的回调
     */
    public void schedule(String alarmTime, TimerTask timerTask) {
        Date date = DateUtil.dateParse(alarmTime, DateUtil.DATE_TIME_PATTERN);

        long delay = date.getTime() - serviceDate.getTime() - getTickCount();

        if (delay < 0) {

        } else {
            new Timer().schedule(timerTask, delay);
        }


    }

    /**
     * 获取内部时钟的日期时间
     *
     * @return
     */
    public Date getInnerClockDate() {
        return DateUtil.dateAddSeconds(serviceDate, period);
    }

}

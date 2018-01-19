/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public final class TimeUtil {

    private TimeUtil() {
    }

    private static StringBuilder mFormatBuilder = new StringBuilder();
    private static Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    public static String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static String msFormat(long timeMs) {
        long totalSeconds = timeMs / 1000;
        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static String getDateTimeEN(long time) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date1 = format1.format(new Date(time));
        return date1;// 2012-10-03 23:41:31
    }

    public static String getDateTimeStr(long time) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = format1.format(new Date(time));
        return date1;// 2012-10-03 23:41:31
    }

    public static String getDateTime(String time) {
        long a = Long.parseLong(time);
        long b = (long) (a * 1000.0);
        return getDateTimeEN(b);
    }

    public static String getDate(String time) {
        long a = Long.parseLong(time);
        long b = (long) (a * 1000.0);
        return getDateTimeStr(b);
    }

//    public static String[] getTimes(String time) {
//        long a = Long.parseLong(time);
//        long b = (long) (a * 1000.0);
//        String str = getDateTimeStr(b);
//        String[] strs = str.split("-");
////        int year = Integer.parseInt(strs[0]);
////        int month = Integer.parseInt(strs[1]);
////        int day = Integer.parseInt(strs[2]);
////        int hour = Integer.parseInt(strs[3]);
////        int second = Integer.parseInt(strs[4]);
//        return strs;
//    }


    public static String getTimeStr(String time) {
        String str = "";
        long[] times = getDistanceTimes(getDate(time), getDate("" + (System.currentTimeMillis() / 1000)));
        if (times[0] < 1) {
            if (times[1] < 1) {
                if (times[2] < 1) {
                    if (times[3] < 1) {
                        if (times[4] < 1) {
                            if (times[5] < 1) {
                                str = times[5] + "秒前";
                            }
                        } else {
                            str = times[4] + "分钟前";
                        }
                    } else {
                        str = times[3] + "小时前";
                    }
                } else {
                    str = times[2] + "天前";
                }
            } else {
                str = times[1] + "月前";
            }
        } else {
            str = times[0] + "年前";
        }
        return str;
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{年，月，天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long year = 0;
        long month = 0;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            year = diff / (365 * 30 * 24 * 60 * 60 * 1000);
            month = diff / (30 * 24 * 60 * 60 * 1000);
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] times = {year, month, day, hour, min, sec};
        return times;
    }

    public static String getCurrentDateTime(long time) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date1 = format1.format(new Date(time));
        return date1;// 2012-10-03 23:41:31
    }

    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String getDateTime(long time) {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        String date1 = format1.format(new Date(time));
        return date1;// 2012-10-03 23:41:31
    }

    public static String getMonthDay(long time) {
        SimpleDateFormat format1 = new SimpleDateFormat("MM月dd日");
        String date1 = format1.format(new Date(time));
        return date1;// 2012-10-03 23:41:31
    }

    public static String getHourMins(long time) {
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
        String date1 = format1.format(new Date(time));
        return date1;// 2012-10-03 23:41:31
    }

    public static int timeForDuration(String time) {
        int duration = 0;
        String[] args = time.split(":");
        int len = args.length;
        if (len == 3) {
            int hourToSedond = Integer.parseInt(args[0]) * 60 * 60;
            int minuteToSecond = Integer.parseInt(args[1]) * 60;
            int second = Integer.parseInt(args[2]);
            duration = (hourToSedond + minuteToSecond + second) * 1000;
        }
        return duration;
    }

    /**
     * 将时间戳转为字符串
     *
     * @param cc_time
     * @return
     */
    public static String getStrTime(long cc_time) {
        return getStrTime(cc_time, "yyyy/MM/dd/ HH:mm:ss");
    }

    public static String getEmailTime(long time) {
        String time1 = getStrTime(time, "yyyy/MM/dd");
        String timeCurrent = getStrTime(System.currentTimeMillis() / 1000, "yyyy/MM/dd");
        if (time1.equals(timeCurrent)) {
            return getStrTime(time, "HH:mm:ss");
        } else {
            return getStrTime(time, "yyyy/MM/dd/ HH:mm:ss");
        }
    }

    /**
     * 将时间戳转为字符串
     *
     * @param cc_time
     * @return
     */
    public static String getStrTime(long cc_time, String format) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        // 例如：cc_time=1291778220
        re_StrTime = sdf.format(new Date(cc_time * 1000L));
        return re_StrTime;
    }

    public static int getCurrentSecconds() {
        long ms = System.currentTimeMillis();
        int s = (int) (ms / 1000);
        return s;
    }

    //时间计数器，最多只能到99小时，如需要更大小时数需要改改方法
    public static String showTimeCount(long time) {
        if (time >= 360000000) {
            return "00:00:00";
        }
        String timeCount = "";
        long hourc = time / 3600000;
        String hour = "0" + hourc;
        hour = hour.substring(hour.length() - 2, hour.length());

        long minuec = (time - hourc * 3600000) / (60000);
        String minue = "0" + minuec;
        minue = minue.substring(minue.length() - 2, minue.length());
        long secc = (time - hourc * 3600000 - minuec * 60000) / 1000;
        String sec = "0" + secc;
        sec = sec.substring(sec.length() - 2, sec.length());
        timeCount = hour + ":" + minue + ":" + sec;
        return timeCount;
    }

    public static String getTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
        return format.format(new Date(time));
    }

    public static String getHourAndMin(long time) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(new Date(time));
    }

    public static String getChatTime(long timesamp) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));
        long time = System.currentTimeMillis();
        switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            case 2:
                result = "前天 " + getHourAndMin(timesamp);
                break;

            default:
                // result = temp + "天前 ";
                result = getTime(timesamp);
                break;
        }

        return result;
    }

    /**
     * 将秒转成分秒
     *
     * @return
     */
    public static String getVoiceRecorderTime(int time) {
        int minute = time / 60;
        int second = time % 60;
        if (minute == 0) {
            return String.valueOf(second);
        }
        return minute + ":" + second;
    }

    public static String getCircleTime(long timesamp) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));
        long time = System.currentTimeMillis();
        switch (temp) {
            case 0:
                result = "今天 " + getHourAndMin(timesamp);
                break;
            case 1:
                result = "昨天 " + getHourAndMin(timesamp);
                break;
            default:
                result = getChinaTime(timesamp);
                break;
        }

        return result;
    }

    /**
     * 并用分割符把时间分成时间数组
     *
     * @param time
     * @return
     */
    public static String[] timestamp(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        String[] fenge = times.split("[年月日时分秒]");
        return fenge;
    }

    public static String getTimeisTodayOrYestday(long timesamp) {
        String result = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        Date today = new Date(System.currentTimeMillis());
        Date otherDay = new Date(timesamp);
        int temp = Integer.parseInt(sdf.format(today))
                - Integer.parseInt(sdf.format(otherDay));
        switch (temp) {
            case 0:
                result = "今天";
                break;
            case 1:
                result = "昨天";
                break;
        }

        return result;
    }

    public static String getChinaTime(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yy年MM月dd日 HH:mm");
        return format.format(new Date(time));
    }

}

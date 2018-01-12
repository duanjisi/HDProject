package com.atgc.hd.comm.utils;

import android.os.Environment;

import java.io.File;
import java.util.Random;

/**
 * 描述： 字符串工具类
 * 作者： liangguokui 2018/1/11
 */
public class StringUtil {
    private static final String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private StringUtil() {

    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    /**
     * 生成长度为10的随机字符串
     * <p>指定长度使用：{@link #getRandomString(int)}
     *
     * @return
     */
    public static String getRandomString() {
        return getRandomString(10);
    }

    /**
     * 生成指定长度的随机字符串
     *
     * @param length 长度
     * @return
     */
    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}

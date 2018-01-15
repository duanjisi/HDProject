package com.atgc.hd.comm.utils;

import java.math.BigInteger;
import java.util.Random;

/**
 * 描述： 字符串工具类
 * 作者： liangguokui 2018/1/11
 */
public class StringUtils {
    private static final String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private StringUtils() {

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


    /**
     * 将byte[]转为各种进制的字符串
     *
     * @param bytes
     *            byte[]
     * @param radix
     *            基数可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }
}

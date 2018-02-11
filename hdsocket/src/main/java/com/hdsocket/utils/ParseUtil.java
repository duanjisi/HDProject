package com.hdsocket.utils;

import java.math.BigInteger;

import io.netty.buffer.ByteBuf;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/2/7
 */
public class ParseUtil {


    /**
     * 将int类型转换成字节数组
     *
     * @param res
     * @return 字节数组
     */
    public static byte[] intToByteArrays(int res) {
        byte[] targets = new byte[4];
//        targets[0] = (byte) (res & 0xff);// 最低位
//        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
//        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
//        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        targets[0] = (byte) ((res >>> 24) & 0xFF);
        targets[1] = (byte) ((res >>> 16) & 0xFF);
        targets[2] = (byte) ((res >>> 8) & 0xFF);
        targets[3] = (byte) (res & 0xff);// 最低位
        return targets;
    }

    /**
     * 将short类型转换的字节数组
     *
     * @param n
     * @return 转换的字节数组
     */
    public static byte[] shortToByteArrays(short n) {
        byte[] b = new byte[2];
        b[0] = (byte) ((n >> 8) & 0xFF);
        b[1] = (byte) (n & 0xFF);
        return b;
    }

    /**
     * byte数组转成字符串
     *
     * @param buffer
     * @param length
     * @return String
     */
    public static String parseString(ByteBuf buffer, int length) {
        byte[] bytes = new byte[length];
        String str;
        try {
            buffer.readBytes(bytes);
            str = new String(bytes, "utf-8");
        } catch (Exception e) {
            return null;
        }
        return str;
    }


    /**
     * byte数组转成10进制数字字符串
     *
     * @param buffer
     * @param length
     * @return String
     */
    public static int parseNumber(ByteBuf buffer, int length) {
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        String result;
        try {
            result = binary(bytes, 10);
        } catch (Exception e) {
            return -1;
        }
        return Integer.valueOf(result);
    }

    /**
     * 将byte[]转为各种进制的字符串
     *
     * @param bytes byte[]
     * @param radix 基数可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix) {
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
    }
}

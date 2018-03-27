package com.atgc.hd.comm.utils;


import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.atgc.hd.HDApplication;
import com.atgc.hd.entity.Header;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

/**
 * <p>描述： 数字转换工具类
 * <p>作者： duanjisi 2018/1/11
 */

public class DigitalUtils {
    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }

    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString 16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] toByteArray(String hexString) {
        if (TextUtils.isEmpty(hexString))
            throw new IllegalArgumentException("this hexString must not be empty");

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {//因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }

        return byteArray;
    }

    /**
     * 字节数组转成16进制表示格式的字符串
     *
     * @param byteArray 要转换的字节数组
     * @return 16进制表示格式的字符串
     **/
    public static String toHexString(byte[] byteArray) {
        if (byteArray == null || byteArray.length < 1)
            throw new IllegalArgumentException("this byteArray must not be null or empty");

        final StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < byteArray.length; i++) {
            if ((byteArray[i] & 0xff) < 0x10)//0~F前面不零
                hexString.append("0");
            hexString.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return hexString.toString().toLowerCase();
    }

    private static String getJson(String cmd, Map<String, String> map) {
        JSONObject object = new JSONObject();
        try {
            object.put("Command", cmd);
            JSONArray array = new JSONArray();
            if (map != null && map.size() != 0) {
                JSONObject obj = new JSONObject(map);
                array.put(obj);
                object.put("Data", array);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }

    /**
     * 根据命令及参数获取所传的信息字节数组
     *
     * @param cmd
     * @param map
     * @return 转换的字节数组
     */
    public static byte[] getBytes(String cmd, Map<String, String> map) {
        String json = getJson(cmd, map);
        byte[] strData = json.getBytes();
        int dataLength = strData.length;
        int crcCode = CRCUtil.crc16CCITTFalse(strData, dataLength);
        byte[] infoHead = constructByteHead(crcCode, dataLength);
        byte[] data = concatByteArrays(infoHead, strData);
        return data;
    }

    public static byte[] getBytes(Object object) {

        String json = JSON.toJSONString(object);

        byte[] strData = json.getBytes();
        int dataLength = strData.length;
        int crcCode = CRCUtil.crc16CCITTFalse(strData, dataLength);
        byte[] infoHead = constructByteHead(crcCode, dataLength);
        byte[] data = concatByteArrays(infoHead, strData);
        return data;
    }

    /**
     * 获取头部的信息的字节数组
     *
     * @param crcCode
     * @param dataLength
     * @return 转换的字节数组
     */
    public static byte[] constructByteHead(int crcCode, int dataLength) {
        Header header = HDApplication.getInstance().getHeader();
        byte[] b = header.getVersion().getBytes();
        byte[] b2 = header.getSrcID().getBytes();
        byte[] b3 = "00000000000000000000".getBytes();

        byte[] b4 = {0};//请求应答
        byte[] b5 = intToByteArrays(5684);//包号
        byte[] b6 = intToByteArrays(dataLength);
        byte[] b7 = shortToByteArrays((short) 0xff);//预留
        byte[] b8 = shortToByteArrays((short) crcCode);//CRC16

        b = concatByteArrays(b, b2);
        b = concatByteArrays(b, b3);
        b = concatByteArrays(b, b4);
        b = concatByteArrays(b, b5);
        b = concatByteArrays(b, b6);
        b = concatByteArrays(b, b7);
        b = concatByteArrays(b, b8);
        return b;
    }

    /**
     * 将两个字节数组拼装成一个字节数组
     *
     * @param b1
     * @param b2
     * @return 拼装好的字节数组
     */
    public static byte[] concatByteArrays(byte[] b1, byte[] b2) {
        byte[] bytes = new byte[b1.length + b2.length];
        System.arraycopy(b1, 0, bytes, 0, b1.length);
        System.arraycopy(b2, 0, bytes, b1.length, b2.length);
        return bytes;
    }

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

}

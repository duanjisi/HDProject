
package com.atgc.hd.comm.utils;

import java.io.UnsupportedEncodingException;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * <p>描述：Base64工具类，
 * <p>作者：duanjisi 2018年 01月 16日
 */
public class Base64Utils {
    // 加密
    public static String encodeBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    // 解密
    public static String getFromBase64(String s) {
        byte[] b = null;
        String result = null;
        if (s != null) {
            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = decoder.decodeBuffer(s);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static String encodeBytes(byte[] b) {
        String s = null;
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }
}

package com.atgc.hd.comm.utils;


import android.text.TextUtils;

import com.atgc.hd.AppApplication;
import com.atgc.hd.entity.Header;
import com.orhanobut.logger.Logger;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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
     * byte[]数组转换为16进制的字符串
     *
     * @param bytes 要转换的字节数组
     * @return 转换后的结果
     */
    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * byte[]数组转换为16进制的字符串
     *
     * @param data 要转换的字节数组
     * @return 转换后的结果
     */
    public static final String byteArrayToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            int v = b & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.getDefault());
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

    public static byte[] getParamBytes(String cmd, Map<String, String> map) {
        String json = getJson(cmd, map);
        Logger.i("info", "===json:" + json);
        int contentLength = json.getBytes().length;
        int crcCode = CRCUtil.crc16CCITTFalse(json.getBytes(), contentLength);
        Header header = AppApplication.getInstance().getHeader();
        final ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(header.getVersion().getBytes());
        byteBuf.writeBytes(header.getSrcID().getBytes());
        byteBuf.writeBytes(header.getDestID().getBytes());
        byteBuf.writeBytes(Int2ByteUtil.intTo1Bytes(Integer.parseInt(header.getRequest())));
        byteBuf.writeBytes(Int2ByteUtil.intTo4Bytes(Integer.parseInt(header.getPackNo())));
//        byteBuf.writeBytes(Int2ByteUtil.intTo4Bytes(Integer.parseInt(header.getContentLength())));
        byteBuf.writeBytes(Int2ByteUtil.intTo4Bytes(contentLength));
        byteBuf.writeBytes(Int2ByteUtil.intTo2Bytes(Integer.parseInt(header.getHold())));
//        byteBuf.writeBytes(Int2ByteUtil.intTo2Bytes(Integer.parseInt(header.getCrc())));
        byteBuf.writeBytes(Int2ByteUtil.intTo2Bytes(crcCode));
        byteBuf.writeBytes(json.getBytes());
        return byteBuf.array();
    }


    private static String getJson(String cmd, Map<String, String> map) {
//        JSONObject Data = JSONObject.fromObject(map);
        JSONObject object = new JSONObject();
        try {
            object.put("Command", cmd);
            if (cmd.equals("COM_DEV_REGISTER")) {
                JSONArray array = new JSONArray();

                JSONObject obj = new JSONObject(map);
                array.put(obj);
                object.put("Data", array);
            } else {
                object.put("Data", map);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }
}

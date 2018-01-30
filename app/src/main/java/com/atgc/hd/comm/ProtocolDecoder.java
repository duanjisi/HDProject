package com.atgc.hd.comm;

import com.atgc.hd.comm.utils.DateUtil;
import com.atgc.hd.comm.utils.StringUtils;
import com.orhanobut.logger.Logger;

import java.util.Date;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * <p>描述：解析工具类
 * <p>作者：duanjisi 2018年 01月 12日
 */
public class ProtocolDecoder {
    private static ByteBuf byteBuf = Unpooled.buffer();

    /**
     * byte数组转成字符串
     *
     * @param buffer
     * @param length
     * @return String
     * @Methods Name parseString
     * @Create In 2017年12月20日 By zhangweixian
     */
    public static String parseString(ByteBuf buffer, int length) {
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        String str = null;
        try {
            str = new String(bytes, "utf-8");
        } catch (Exception e) {
            Logger.d("时间为" + DateUtil.getDate(new Date()) + "解码器转换字符串发生异常:" + e.getMessage());
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
     * @Methods Name parseNumber
     * @Create In 2017年12月20日 By zhangweixian
     */
    public static String parseNumber(ByteBuf buffer, int length) {
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        String result = null;
        try {
            result = StringUtils.binary(bytes, 10);
        } catch (Exception e) {
            Logger.d("时间为" + DateUtil.getDate(new Date()) + "解码器转换数字异常:" + e.getMessage());
            return null;
        }
        return result;
    }

    /**
     * @param bytes
     * @return String
     */
    public static String parseContent(byte[] bytes) {
        ByteBuf byteBuf = Unpooled.buffer(bytes.length);
        byteBuf.writeBytes(bytes);
        String version = parseString(byteBuf, 4);
        String srcID = parseString(byteBuf, 20);
        String destID = parseString(byteBuf, 20);
        String request = parseNumber(byteBuf, 1);
        String packNo = parseNumber(byteBuf, 4);
        String contentLength = parseNumber(byteBuf, 4);
        String hold = parseNumber(byteBuf, 2);
        String crc = parseNumber(byteBuf, 2);
        //内容长度
        int length = Integer.valueOf(contentLength);
        // 读取消息内容
        String content = parseString(byteBuf, length);
        byteBuf.readableBytes();
        byteBuf.clear();
        return content;
    }

}
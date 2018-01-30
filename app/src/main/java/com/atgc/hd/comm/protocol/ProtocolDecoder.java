package com.atgc.hd.comm.protocol;

import com.alibaba.fastjson.JSONObject;
import com.atgc.hd.comm.utils.DateUtil;
import com.atgc.hd.comm.utils.StringUtils;
import com.orhanobut.logger.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
     * 默认头部长度.
     */
    public static final int BASE_LENGTH = 57;
    // 存储分包的后半截内容数据
    private static Map<String, Object> map = new HashMap<>();

    public static ProtocolBody decode(byte[] bytes) {

        ByteBuf buffer = Unpooled.buffer(bytes.length);
        buffer.writeBytes(bytes);

        // 分包
        if (!map.isEmpty()) {
            ProtocolHeader header = (ProtocolHeader) map.get("header");
            // 分包时已读内容
            String prefixContext = (String) map.get("prefixContext");
            // 分包时未读内容长度
            int suffixSize = Integer.parseInt(map.get("suffixSize").toString());
            // 分包时未读内容
            String suffixContent = parseString(buffer, suffixSize);
            ProtocolBody message = new ProtocolBody(header, prefixContext + suffixContent);
            message.setCommand(getCommand(message.getContent()));
            map.clear();
            return message;
        }

        if (buffer.readableBytes() >= BASE_LENGTH) {
            // 暂时不考虑分包、粘包问题
            String version = parseString(buffer, 4);
            String srcID = parseString(buffer, 20);
            String destID = parseString(buffer, 20);

            String request = parseNumber(buffer, 1);
            String packNo = parseNumber(buffer, 4);
            String contentLength = parseNumber(buffer, 4);
            String hold = parseNumber(buffer, 2);
            String crc = parseNumber(buffer, 2);

            // 组装协议头
            ProtocolHeader header = new ProtocolHeader();
            header.setVersion(version);
            header.setSrcID(srcID);
            header.setDestId(destID);

            header.setContentLength(contentLength);
            header.setCrc16(crc);
            header.setHold(hold);
            header.setIsRequest(request);
            header.setPackageNo(packNo);

            // 分包处理
            // 分包时已读内容长度
            // 内容长度
            long length = Long.parseLong(contentLength);
            int prefixSize = buffer.writerIndex() - buffer.readerIndex();

            if (prefixSize < length) {
                String tempContent = parseString(buffer, prefixSize);
                // 完整包头信息
                map.put("header", header);
                // 分包时已读内容
                map.put("prefixContext", tempContent);
                // 分包时未读内容长度
                map.put("suffixSize", length - prefixSize);
                return null;
            }

            // 读取消息内容
            String content = parseString(buffer, (int) length);
            buffer.readableBytes();
            ProtocolBody message = new ProtocolBody(header, content);
            message.setCommand(getCommand(content));
            return message;
        }

        return null;
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

        Logger.e("------------------------" + bytes.length + "  " + length);
        // 读取消息内容
        String content;

        if (bytes.length < length) {
            content = parseString(byteBuf, bytes.length - 57);
        } else {
            content = parseString(byteBuf, length);
        }
        Logger.e("------------------------" + content);

        byteBuf.readableBytes();
        byteBuf.clear();
        return content;
    }

    /**
     * 得到命令.
     *
     * @param content required
     * @return String
     */
    private static String getCommand(String content) {
        JSONObject obj = JSONObject.parseObject(content);
        if (StringUtils.isEmpty(content)) {
            return null;
        }
        return obj.getString("Command");
    }
}
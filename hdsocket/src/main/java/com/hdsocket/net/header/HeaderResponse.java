package com.hdsocket.net.header;


import com.hdsocket.utils.ParseUtil;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * <p>描述：响应报文的包头
 * <p>作者：liangguokui 2018/2/7
 */
public class HeaderResponse {
    // 协议版本-HDXM
    public String version;
    // 源ID
    public String srcID;
    // 目的ID
    public String destID;
    // 请求/应答
    public int status;
    // 包号
    public int packageNo;
    // 数据字段长度
    public int contentLength;
    // 保留
    public String hold;
    // CRC16
    public String crc;

    public void parse(byte[] data) {
        ByteBuf byteBuf = Unpooled.buffer(data.length);
        byteBuf.writeBytes(data);
        version = ParseUtil.parseString(byteBuf, 4);
        srcID = ParseUtil.parseString(byteBuf, 20);
        destID = ParseUtil.parseString(byteBuf, 20);
        status = ParseUtil.parseNumber(byteBuf, 1);
        packageNo = ParseUtil.parseNumber(byteBuf, 4);
        contentLength = ParseUtil.parseNumber(byteBuf, 4);
        hold = ParseUtil.parseString(byteBuf, 2);
        crc = ParseUtil.parseString(byteBuf, 2);
    }


}

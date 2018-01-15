/**
 * Summary: 响应协议内容反序列化对象（不包含data字段，方便提前状态码检测）
 */

package com.atgc.hd.comm.net;

public class PreRspPojo {
    public String Command;
    public String Result;
    public String Data;
    public String ErrorCode;
    public String ErrorMessage;
}
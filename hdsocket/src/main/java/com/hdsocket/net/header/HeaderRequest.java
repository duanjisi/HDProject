package com.hdsocket.net.header;

import com.hdsocket.Config;

/**
 * <p>描述：请求报文的报文
 * <p>作者：liangguokui 2018/2/7
 */
public class HeaderRequest {
    private String version = "";
    private String srcID = "";
    private String destID = "";
    private String request = "";
    private String packNo = "";
    private String contentLength = "";
    private String hold = "";
    private String crc = "";

    public static HeaderRequest defaultHeader() {
        HeaderRequest header = new HeaderRequest();
        header.setVersion("HDXM");
        header.setSrcID(Config.deviceId);
        header.setDestID("1004201658FCDBD8341E");
        header.setRequest("1");
        header.setHold("0");
        header.setPackNo("172");
        header.setCrc("42027");
        return header;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSrcID() {
        return srcID;
    }

    public void setSrcID(String srcID) {
        this.srcID = srcID;
    }

    public String getDestID() {
        return destID;
    }

    public void setDestID(String destID) {
        this.destID = destID;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getPackNo() {
        return packNo;
    }

    public void setPackNo(String packNo) {
        this.packNo = packNo;
    }

    public String getContentLength() {
        return contentLength;
    }

    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

    public String getHold() {
        return hold;
    }

    public void setHold(String hold) {
        this.hold = hold;
    }

    public String getCrc() {
        return crc;
    }

    public void setCrc(String crc) {
        this.crc = crc;
    }
}

package com.atgc.hd.entity;

/**
 * 描述： 协议头部信息
 * 作者： duanjisi  2018/1/12
 */

public class Header {
    private String version = "";
    private String srcID = "";
    private String destID = "";
    private String request = "";
    private String packNo = "";
    private String contentLength = "";
    private String hold = "";
    private String crc = "";

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

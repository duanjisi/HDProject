/**
 * Copyright 2017-2025 Evergrande Group.
 */

package com.atgc.hd.comm.protocol;

import java.io.Serializable;

/**
 * 协议头实体类.
 * 
 * @Author wangguojun
 * @Create In 2018年1月4日
 */
public class ProtocolHeader implements Serializable {
  private static final long serialVersionUID = 1L;
  // 协议版本
  private String version;
  // 目标ID
  private String destId;
  // 源ID
  private String srcID;
  // 请求或应答
  private String isRequest;
  // 包号
  private String packageNo;
  // 数据长度
  private String contentLength;
  // 预留
  private String hold;
  // crc校验
  private String crc16;

  public void setDestId(String destId) {
    this.destId = destId;
  }

  public void setSrcID(String srcID) {
    this.srcID = srcID;
  }

  public String getDestId() {
    return destId;
  }

  public String getSrcID() {
    return srcID;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public String getIsRequest() {
    return isRequest;
  }

  public void setIsRequest(String isRequest) {
    this.isRequest = isRequest;
  }

  public String getPackageNo() {
    return packageNo;
  }

  public void setPackageNo(String packageNo) {
    this.packageNo = packageNo;
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

  public String getCrc16() {
    return crc16;
  }

  public void setCrc16(String crc16) {
    this.crc16 = crc16;
  }
}

/**
 * Copyright 2017-2025 Evergrande Group.
 */

package com.atgc.hd.comm.protocol;

import java.io.Serializable;

/**
 * 协议体实体类.
 * 
 * @Author wangguojun
 * @Create In 2018年1月4日
 */
public class ProtocolBody implements Serializable {
  private static final long serialVersionUID = 1L;
  private ProtocolHeader protocolHeader;

  /**
   * 消息的内容.
   */
  private String content;

  /**
   * 协议命令ID, 需要从content中解析出来.
   */
  private String command;

  /**
   * 用于初始化，SmartCarProtocol.
   * 
   * @param protocolHeader 协议里面，消息数据的长度
   * @param content 协议里面，消息的数据
   */
  public ProtocolBody(ProtocolHeader protocolHeader, String content) {
    this.protocolHeader = protocolHeader;
    this.content = content;
  }


  public ProtocolHeader getProtocolHeader() {
    return protocolHeader;
  }

  public void setProtocolHeader(ProtocolHeader protocolHeader) {
    this.protocolHeader = protocolHeader;
  }

  public String getContent() {
    return content;
  }



  public void setContent(String content) {
    this.content = content;
  }



  @Override
  public String toString() {
    return String.format(
        "[version=%s,contentLength=%s,srcId=%s,destId=%s,Hold=%s,Request=%s,PackageNo=%s"
        + ",Crc16=%s,content=%s]",
        protocolHeader.getVersion(), protocolHeader.getContentLength(), protocolHeader.getSrcID(),
        protocolHeader.getDestId(), protocolHeader.getHold(), protocolHeader.getIsRequest(),
        protocolHeader.getPackageNo(), protocolHeader.getCrc16(), content);
  }


  public String getCommand() {
    return command;
  }


  public void setCommand(String command) {
    this.command = command;
  }
}

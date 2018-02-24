package com.atgc.hd.comm.net.request.base;

import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.utils.DigitalUtils;
import com.hdsocket.Config;

/**
 * <p>描述：请求报文的报文
 * <p>作者：liangguokui 2018/2/7
 */
public class HeaderRequest {
    private String version = "HDXM";
    private String srcID = DeviceParams.getInstance().getDeviceId();
    private String destID = "00000000000000000000";
    private String request = "0";
    private String packNo = "5684";
    private int contentLength;
    private byte[] hold = new byte[2];
    private byte[] crc = new byte[2];

    public static HeaderRequest defaultHeader() {
        HeaderRequest header = new HeaderRequest();
        header.setVersion("HDXM");
        header.setSrcID(Config.deviceId);
        header.setDestID("00000000000000000000");
        header.setRequest("0");
        header.setPackNo("5684");
        header.setHold(new byte[2]);
        header.setCrc(new byte[2]);
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

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getHold() {
        return hold;
    }

    public void setHold(byte[] hold) {
        this.hold = hold;
    }

    public byte[] getCrc() {
        return crc;
    }

    public void setCrc(byte[] crc) {
        this.crc = crc;
    }

    public byte[] toBytes() {

        byte[] headerByte = new byte[57];
        copy(version.getBytes(), headerByte, 0);
        copy(srcID.getBytes(), headerByte, 4);
        copy(destID.getBytes(), headerByte, 24);
        copy(request.getBytes(), headerByte, 44);
        copy(packNo.getBytes(), headerByte, 45);
        copy(DigitalUtils.intToByteArrays(contentLength), headerByte, 49);
        copy(hold, headerByte, 53);
        copy(crc, headerByte, 55);

        return headerByte;
    }

    private void copy(byte[] src, byte[] dest, int startIndex) {
        System.arraycopy(src, 0, dest, startIndex, src.length);
    }
}

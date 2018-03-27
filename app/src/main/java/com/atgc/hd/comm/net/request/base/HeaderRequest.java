package com.atgc.hd.comm.net.request.base;

import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.utils.DigitalUtils;

/**
 * <p>描述：请求报文的报文
 * <p>作者：liangguokui 2018/2/7
 */
public class HeaderRequest {
    private String version = "HDXM";
    private String srcID = DeviceParams.getInstance().getDeviceId();
    private String destID = "00000000000000000000";
    private int request = 0;
    private int packNo = 5684;
    private int contentLength;
    private byte[] hold = new byte[2];
    private byte[] crc = new byte[2];

    public static HeaderRequest defaultHeader() {
        HeaderRequest header = new HeaderRequest();
        header.setVersion("HDXM");
        header.setSrcID(DeviceParams.getInstance().getDeviceId());
        header.setDestID("00000000000000000000");
        header.setRequest(0);
        header.setPackNo(5684);
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

    public int getRequest() {
        return request;
    }

    public void setRequest(int request) {
        this.request = request;
    }

    public int getPackNo() {
        return packNo;
    }

    public void setPackNo(int packNo) {
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
        copy(DigitalUtils.intToByteArrays(request), headerByte, 44);
        copy(DigitalUtils.intToByteArrays(packNo), headerByte, 45);
        copy(DigitalUtils.intToByteArrays(contentLength), headerByte, 49);
        copy(hold, headerByte, 53);
        copy(crc, headerByte, 55);

        return headerByte;
    }

    private void copy(byte[] src, byte[] dest, int startIndex) {
        System.arraycopy(src, 0, dest, startIndex, src.length);
    }

    /**
     48 44 58 4d
     31 30 30 31 32 30 31 37 30 32 30 30 30 30 30 30 30 30 30 30
     30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30
     30
     35 36 38 34
     00 00 02 7d
     00 00
     43 40
     ef bf bd 29 ef bf bd ef bf bd 17 19 ef bf bd 6e 11 34 ef bf bd ef bf bd ef bf bd 6d ef bf bd ef bf bd 1d 50 66 76 2a 77 ef bf bd 7d 28 ef bf bd 4a 76 ef bf bd ef bf bd 14 ef bf bd 73 ef bf bd 5b 23 ef bf bd 12 ef


     */
}

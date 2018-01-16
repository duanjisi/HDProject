package com.atgc.hd;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.atgc.hd.comm.ProtocolDecoder;
import com.atgc.hd.comm.utils.DigitalUtils;
import com.atgc.hd.comm.utils.Int2ByteUtil;
import com.atgc.hd.entity.Header;
import com.orhanobut.logger.Logger;

import java.io.UnsupportedEncodingException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by duanjisi on 2018/1/12.
 */

public class FouthActivity extends Activity {
    private static final String json = "{\n" +
            "\t\"Command\":\"COM_LOAD_CERTIFICATE\",\n" +
            "\t\"Data\":\n" +
            "\t{\n" +
            "\t\t\"StartTime\":\"2017-11-10\",\n" +
            "\t\t\"EndTime\":\"2020-11-10\",\n" +
            "\t\t\"UserType\":\"MONTH_A\",\n" +
            "\t\t\"CredenceType:\"CAR_PLATE\",\n" +
            "\t\t\"CredenceNo\":\"粤A123456\"\n" +
            "\t\t\"UserName\":\"张三\"\n" +
            "\t\t\"UserNo\":\"1002\"\n" +
            "\t\t\"OpTime\":\"2017-11-10 08:00:01\"\n" +
            "\t}\n" +
            "}\n";

    private byte[] arr = new byte[]{0x48, 0x44, 0x58, 0x4d, 0x31, 0x30, 0x30, 0x34, 0x32, 0x30, 0x31, 0x36, 0x35, 0x38, 0x46, 0x43
            , 0x44, 0x42, 0x44, 0x38, 0x33, 0x34, 0x31, 0x45, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30
            , 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x30, 0x00, 0x00, 0x00, 0x00
            , (byte) 0xac, 0x00, 0x00, 0x01, (byte) 0x84, 0x00, 0x00, (byte) 0xa4, 0x2b, 0x7b, 0x0a, 0x09, 0x22, 0x43, 0x6f, 0x6d
            , 0x6d, 0x61, 0x6e, 0x64, 0x22, 0x3a, 0x09, 0x22, 0x43, 0x4f, 0x4d, 0x5f, 0x44, 0x45, 0x56, 0x5f
            , 0x52, 0x45, 0x47, 0x49, 0x53, 0x54, 0x45, 0x52, 0x22, 0x2c, 0x0a, 0x09, 0x22, 0x44, 0x61, 0x74
            , 0x61, 0x22, 0x3a, 0x09, 0x5b, 0x7b, 0x0a, 0x09, 0x09, 0x09, 0x22, 0x74, 0x79, 0x70, 0x65, 0x22
            , 0x3a, 0x09, 0x35, 0x2c, 0x0a, 0x09, 0x09, 0x09, 0x22, 0x68, 0x61, 0x73, 0x49, 0x6e, 0x69, 0x74
            , 0x22, 0x3a, 0x09, 0x31, 0x2c, 0x0a, 0x09, 0x09, 0x09, 0x22, 0x64, 0x65, 0x76, 0x69, 0x63, 0x65
            , 0x49, 0x44, 0x22, 0x3a, 0x09, 0x22, 0x31, 0x30, 0x30, 0x34, 0x32, 0x30, 0x31, 0x36, 0x35, 0x38
            , 0x46, 0x43, 0x44, 0x42, 0x44, 0x38, 0x33, 0x34, 0x31, 0x45, 0x22, 0x2c, 0x0a, 0x09, 0x09, 0x09
            , 0x22, 0x6d, 0x75, 0x66, 0x61, 0x63, 0x74, 0x75, 0x72, 0x65, 0x72, 0x22, 0x3a, 0x09, 0x22, 0x6c
            , 0x65, 0x65, 0x6c, 0x65, 0x6e, 0x22, 0x2c, 0x0a, 0x09, 0x09, 0x09, 0x22, 0x6d, 0x61, 0x63, 0x4e
            , 0x4f, 0x22, 0x3a, 0x09, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x2c, 0x0a, 0x09, 0x09, 0x09, 0x22
            , 0x6c, 0x6f, 0x63, 0x61, 0x74, 0x69, 0x6f, 0x6e, 0x41, 0x64, 0x64, 0x72, 0x22, 0x3a, 0x09, 0x22
            , 0x6c, 0x6f, 0x67, 0x69, 0x63, 0x5f, 0x61, 0x64, 0x64, 0x72, 0x31, 0x32, 0x33, 0x34, 0x22, 0x2c
            , 0x0a, 0x09, 0x09, 0x09, 0x22, 0x6e, 0x61, 0x6d, 0x65, 0x22, 0x3a, 0x09, 0x22, 0x65, 0x6c, 0x65
            , 0x76, 0x61, 0x74, 0x6f, 0x72, 0x20, 0x63, 0x6f, 0x6e, 0x74, 0x72, 0x6f, 0x6c, 0x22, 0x2c, 0x0a
            , 0x09, 0x09, 0x09, 0x22, 0x67, 0x61, 0x74, 0x65, 0x57, 0x61, 0x79, 0x22, 0x3a, 0x09, 0x22, 0x31
            , 0x39, 0x32, 0x2e, 0x31, 0x36, 0x38, 0x2e, 0x32, 0x30, 0x30, 0x2e, 0x32, 0x35, 0x34, 0x22, 0x2c
            , 0x0a, 0x09, 0x09, 0x09, 0x22, 0x69, 0x70, 0x22, 0x3a, 0x09, 0x22, 0x31, 0x39, 0x32, 0x2e, 0x31
            , 0x36, 0x38, 0x2e, 0x32, 0x30, 0x30, 0x2e, 0x31, 0x31, 0x31, 0x22, 0x2c, 0x0a, 0x09, 0x09, 0x09
            , 0x22, 0x6d, 0x61, 0x63, 0x22, 0x3a, 0x09, 0x22, 0x35, 0x38, 0x3a, 0x46, 0x43, 0x3a, 0x44, 0x42
            , 0x3a, 0x44, 0x38, 0x3a, 0x33, 0x34, 0x3a, 0x31, 0x65, 0x22, 0x2c, 0x0a, 0x09, 0x09, 0x09, 0x22
            , 0x6d, 0x61, 0x73, 0x6b, 0x22, 0x3a, 0x09, 0x22, 0x32, 0x35, 0x35, 0x2e, 0x32, 0x35, 0x35, 0x2e
            , 0x32, 0x35, 0x35, 0x2e, 0x30, 0x22, 0x2c, 0x0a, 0x09, 0x09, 0x09, 0x22, 0x76, 0x65, 0x72, 0x73
            , 0x69, 0x6f, 0x6e, 0x22, 0x3a, 0x09, 0x22, 0x56, 0x31, 0x2e, 0x38, 0x35, 0x5f, 0x32, 0x30, 0x31
            , 0x37, 0x31, 0x32, 0x30, 0x31, 0x22, 0x0a, 0x09, 0x09, 0x7d, 0x5d, 0x0a, 0x7d};

    private Button btn1, btn2, btn3;
    private TextView tv_str;
    private byte[] strs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fouth_activity);
        initViews();
    }

    private void initViews() {

        final ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(arr);

        tv_str = findViewById(R.id.tv_str);
        btn1 = findViewById(R.id.button1);
        btn2 = findViewById(R.id.button2);
        btn3 = findViewById(R.id.button3);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                strs = DigitalUtils.hexStringToByteArray("0x0000002");
//                tv_str.setText(strs.toString());

//                String version = ProtocolDecoder.parseString(byteBuf, 4);
//                String srcID = ProtocolDecoder.parseString(byteBuf, 20);
//                String destID = ProtocolDecoder.parseString(byteBuf, 20);
//                String request = ProtocolDecoder.parseNumber(byteBuf, 1);
//                String packNo = ProtocolDecoder.parseNumber(byteBuf, 4);
//                String contentLength = ProtocolDecoder.parseNumber(byteBuf, 4);
//                String hold = ProtocolDecoder.parseNumber(byteBuf, 2);
//                String crc = ProtocolDecoder.parseNumber(byteBuf, 2);
//                Logger.i("info", "version:" + version + "\n" + "srcID:" + srcID + "\n" + "destID:" + destID);
                byte[] aar = getBytes();
                Logger.i("info", "aar:" + aar);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (strs != null && strs.length != 0) {
                String str = DigitalUtils.toHexString(arr);
                tv_str.setText(str);
//                }
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byte[] aa = new byte[0];
                try {
                    aa = json.getBytes("utf-8");
                    tv_str.setText(aa.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Header getHeader() {
        Header header = new Header();
        header.setVersion("HDXM");
        header.setSrcID("1004201658FCDBD8341E");
        header.setDestID("00000000000000000001");
        header.setRequest("0");
        header.setPackNo("172");
        header.setContentLength("388");
        header.setHold("0");
        header.setCrc("42027");
        return header;
    }


    private byte[] getBytes() {
        Header header = getHeader();
        final ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(header.getVersion().getBytes());
        byteBuf.writeBytes(header.getSrcID().getBytes());
        byteBuf.writeBytes(header.getDestID().getBytes());
        byteBuf.writeBytes(Int2ByteUtil.intTo1Bytes(Integer.parseInt(header.getRequest())));
        byteBuf.writeBytes(Int2ByteUtil.intTo4Bytes(Integer.parseInt(header.getPackNo())));
        byteBuf.writeBytes(Int2ByteUtil.intTo4Bytes(Integer.parseInt(header.getContentLength())));
        byteBuf.writeBytes(Int2ByteUtil.intTo2Bytes(Integer.parseInt(header.getHold())));
        byteBuf.writeBytes(Int2ByteUtil.intTo2Bytes(Integer.parseInt(header.getCrc())));
        byteBuf.writeBytes(json.getBytes());
        return byteBuf.array();
    }
}
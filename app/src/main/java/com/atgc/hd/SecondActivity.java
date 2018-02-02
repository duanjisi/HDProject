package com.atgc.hd;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.atgc.hd.comm.protocol.ProtocolDecoder;
import com.atgc.hd.comm.utils.DigitalUtils;
import com.atgc.hd.comm.utils.Int2ByteUtil;
import com.atgc.hd.entity.Header;
import com.orhanobut.logger.Logger;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * Created by duanjisi on 2018/1/11.
 */

public class SecondActivity extends Activity {
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
    //    private byte[] arr = null;
    private TextView tvRegister, txt1;
    private static final String HOST = "172.16.10.80";
    private static final int PORT = 20001;
    private static final int TYPE_TXT_MSG = 1;
    private static final int TYPE_BYTE_MSG = 2;

    Socket socket = null;
    //    BufferedReader br = null;
    DataInputStream in = null;
    String buffer = "";
    public Handler myHandler = new Handler() {
        final ByteBuf byteBuf = Unpooled.buffer();

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x11) {
                int type = msg.arg1;
                Bundle bundle = msg.getData();
                if (type == TYPE_TXT_MSG) {
                    txt1.append("server:" + bundle.getString("msg") + "\n");
                } else if (type == TYPE_BYTE_MSG) {
                    byte[] bytes = bundle.getByteArray("bytes");
                    byteBuf.writeBytes(bytes);
                    String version = ProtocolDecoder.parseString(byteBuf, 4);
                    String srcID = ProtocolDecoder.parseString(byteBuf, 20);
                    String destID = ProtocolDecoder.parseString(byteBuf, 20);
                    String request = ProtocolDecoder.parseNumber(byteBuf, 1);
                    String packNo = ProtocolDecoder.parseNumber(byteBuf, 4);
                    String contentLength = ProtocolDecoder.parseNumber(byteBuf, 4);
                    String hold = ProtocolDecoder.parseNumber(byteBuf, 2);
                    String crc = ProtocolDecoder.parseNumber(byteBuf, 2);

                    StringBuilder sb = new StringBuilder();

                    sb.append("version:" + version + "\n");
                    sb.append("srcID:" + srcID + "\n");
                    sb.append("destID:" + destID + "\n");
                    sb.append("request:" + request + "\n");
                    sb.append("contentLength:" + contentLength + "\n");
                    sb.append("hold:" + hold + "\n");
                    sb.append("packNo:" + packNo + "\n");
                    sb.append("crc:" + crc + "\n");
                    String str = sb.toString();
                    Logger.i("info", "=====str:" + str);
                    txt1.append(str);
                }
            }
        }

    };

    private boolean running = true;
    private Button btn_Param;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        initViews();
    }

    private void initViews() {
//        arr = getBytes();
        Button btnParam = findViewById(R.id.btn_Param);
        tvRegister = findViewById(R.id.tv_device_register);
        txt1 = findViewById(R.id.tv_str);
        btnParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testParam();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MyThread().start();
            }
        });
    }


    private void testParam() {
        HashMap<String, String> map = new HashMap<>();
        map.put("StartTime", "2017-11-10");
        map.put("EndTime", "2017-11-10");
        map.put("UserType", "MONTH_A");
        map.put("CredenceType", "CAR_PLATE");
        map.put("CredenceNo", "湘 A123456");
        map.put("UserName", "张三");
        map.put("UserNo", "1006");
        map.put("OpTime", "2017-11-10 08:00:01");
        DigitalUtils.getParamBytes("COM_LOAD_CERTIFICATE", map);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        running = false;
    }

    class MyThread extends Thread {

        public String txt1;

        public MyThread() {
        }

        @Override
        public void run() {
            //定义消息
            final Message msg = new Message();
            msg.what = 0x11;
            Bundle bundle = new Bundle();
            bundle.clear();
            try {
                //连接服务器 并设置连接超时为5秒
//                socket = new Socket();
                socket = new Socket(HOST, PORT);
//                socket.connect(new InetSocketAddress(HOST, PORT), 5000);
//                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                in = new DataInputStream(socket.getInputStream());
                //获取输入输出流
                OutputStream ou = socket.getOutputStream();
                BufferedReader bff = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                //读取发来服务器信息
//                String line = null;
//                buffer = "";
//                while ((line = bff.readLine()) != null) {
//                    buffer = line + buffer;
//                }
                //向服务器发送信息
//                ou.write("android 客户端".getBytes("gbk"));
                byte[] acceptdata1 = null;
                ou.write(arr);
                while (running) {
                    String content = null;
                    try {
                        int datalength = in.available();
                        if (datalength > 0) {
                            acceptdata1 = new byte[1024];
                            if (in.read(acceptdata1) != -1) {
                                Message Msg = myHandler.obtainMessage();
                                Msg.what = 0x11;
                                bundle.putByteArray("bytes", acceptdata1);
                                Msg.setData(bundle);
                                Msg.arg1 = TYPE_BYTE_MSG;
                                myHandler.sendMessage(Msg);
                            }
                            acceptdata1 = null;
                        }
//                        String str = br.readLine();
//                        Logger.i("info" + "============content:" + br.readLine());
//                        while ((content = br.readLine()) != null) {
//                            // 界面显示该数据
////                                Message msg = new Message();
////                                msg.what = 0x123;
//                            msg.obj = content;
//                            myHandler.sendMessage(msg);
//                        }

                    } catch (IOException io) {
                        io.printStackTrace();
                    }
                }
                ou.flush();
//                bundle.putString("msg", buffer.toString());
//                msg.setData(bundle);
//                //发送消息 修改UI线程中的组件
//                myHandler.sendMessage(msg);
                //关闭各种输入输出流
                bff.close();
                ou.close();
                socket.close();
            } catch (SocketTimeoutException aa) {
                String str = aa.getMessage();
                Log.i("info", "exception:DDDD" + aa.getMessage());
                //连接超时 在UI界面显示消息
                bundle.putString("msg", "服务器连接失败！请检查网络是否打开");
                msg.setData(bundle);
                msg.arg1 = TYPE_TXT_MSG;
                //发送消息 修改UI线程中的组件
                myHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                bundle.putString("msg", e.getMessage());
                msg.setData(bundle);
                msg.arg1 = TYPE_TXT_MSG;
                //发送消息 修改UI线程中的组件
                myHandler.sendMessage(msg);
            }
        }
    }

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


    private Header getHeader() {
        Header header = new Header();
        header.setVersion("HD");
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

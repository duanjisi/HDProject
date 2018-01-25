/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.net;

import com.atgc.hd.comm.utils.FileUtil;
import com.orhanobut.logger.Logger;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * <p>描述：
 * <p>作者：duanjisi 2018年 01月 17日
 */

public abstract class SocketTransceiver implements Runnable {
    private Socket socket;
    private DataInputStream dataInputStream;
    //    private DataOutputStream dataOutputStream;
    private BufferedOutputStream bufferedOutputStream;
    private InputStream inputStream;
    private OutputStream outputStream;
    private boolean runFlag = false;

    public void start(Socket socket) {
        this.socket = socket;
        runFlag = true;
        new Thread(this).start();
    }

    public void stop() {
        runFlag = false;
        try {
            socket.shutdownInput();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            dataInputStream = new DataInputStream(inputStream);
        } catch (IOException e) {
            runFlag = false;
            e.printStackTrace();
        }
        byte[] datas;
        while (runFlag) {
            //线程死循环  不停的从服务器端读回数据,并通过onReceiver()方法回传显示到UI
            try {
//                String s = dataInputStream.readUTF();
                int lenth = dataInputStream.available();
                if (lenth > 0) {
                    datas = new byte[lenth];
                    if (dataInputStream.read(datas) != -1) {
                        this.onReceive(datas);
                    }
                }
//                this.onReceive(s);
            } catch (IOException e) {
                runFlag = false;
                e.printStackTrace();
            }
        }
        //断开连接
        FileUtil.closeQuietly(inputStream);
        FileUtil.closeQuietly(outputStream);
        FileUtil.closeQuietly(dataInputStream);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.onConnectBreak();//连接被动断开
    }

//    public void sendMSG(String s) {
//        if (outputStream != null) {
//            dataOutputStream = new DataOutputStream(outputStream);
//            try {
//                dataOutputStream.writeUTF(s);
//                dataOutputStream.flush();
//                this.onSendSuccess(s.getBytes());
//            } catch (IOException e) {
//                onConnectBreak();
//                runFlag = false;
//                e.printStackTrace();
//            }
//        }
//    }

    public void sendMSG(final byte[] bytes) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (outputStream != null) {
                    bufferedOutputStream = new BufferedOutputStream(outputStream);

                    try {
                        bufferedOutputStream.write(bytes, 0, bytes.length);
                        bufferedOutputStream.flush();
//                        this.onSendSuccess(bytes);
                    } catch (IOException e) {
                        onConnectBreak();
                        runFlag = false;
                        e.printStackTrace();
                        Logger.e("发送异常。。。。");
                    }
                } else {
                    Logger.e("连接已断开，发送失败。。。。");
                }
            }
        }).start();
    }

    public abstract void onReceive(byte[] s);

    public abstract void onConnectBreak();

    public abstract void onSendSuccess(byte[] s);
}

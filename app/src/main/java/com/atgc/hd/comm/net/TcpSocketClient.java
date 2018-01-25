/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.net;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.atgc.hd.comm.ProtocolDecoder;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>描述：
 * <p>作者：duanjisi 2018年 01月 17日
 */

public class TcpSocketClient implements Runnable {
    private String IP;
    private int port;
    private SocketTransceiver transceiver;
    private Socket socket;
    private TcpListener listener;
    private Map<String, OnReceiveListener> mapListener;

    /**
     * <p>注册监听器，用于从网关发送数据过来时回调
     * <p>注：当不再使用时务必调用{@link #unregisterOnReceiveListener(String...)}注销
     *
     * @param listener
     * @param cmd      命令字，必填，不能为空
     * @param cmds     可填写多个接口命令字
     */
    public void registerOnReceiveListener(OnReceiveListener listener, String cmd, String... cmds) {
        if (cmd == null) {
            throw new NullPointerException("注册监听命令字不能为空");
        } else {
            mapListener.put(cmd, listener);
        }

        for (String temp : cmds) {
            mapListener.put(temp, listener);
        }
    }

    /**
     * 注销监听
     *
     * @param cmds
     */
    public void unregisterOnReceiveListener(String... cmds) {
        for (String cmd : cmds) {
            mapListener.remove(cmd);
        }
    }

    public void setListener(TcpListener listener) {
        this.listener = listener;
    }

    private static TcpSocketClient tcpSocketClient = null;

    private TcpSocketClient() {
        mapListener = new HashMap<>();
    }

    public synchronized static TcpSocketClient getInstance() {
        if (tcpSocketClient == null) {
            tcpSocketClient = new TcpSocketClient();
        }
        return tcpSocketClient;
    }

    public void connect(String IP, int port) {
        this.IP = IP;
        this.port = port;
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
            transceiver = new SocketTransceiver() {
                @Override
                public void onReceive(byte[] bytes) {

                    if (bytes != null && bytes.length != 0) {

                        final String content = ProtocolDecoder.parseContent(bytes);

                        Logger.e("content: " + content);

                        if (!TextUtils.isEmpty(content)) {
                            PreRspPojo preRspPojo = JSON.parseObject(content, PreRspPojo.class);
                            preRspPojo.originJson = content;
                            if (listener != null) {
                                listener.onReceive(preRspPojo);
                            }

                            onReceive(preRspPojo);
                        }
                    }
                }

                private void onReceive(PreRspPojo preRspPojo) {
                    if (mapListener.containsKey(preRspPojo.Command)) {
                        OnReceiveListener listener = mapListener.get(preRspPojo.Command);
                        listener.onReceive(preRspPojo.Command, preRspPojo.Data);
                    }
                }

                @Override
                public void onConnectBreak() {
                    if (listener != null) {
                        listener.onConnectBreak();
                    }
                }

                @Override
                public void onSendSuccess(byte[] s) {
                    if (listener != null) {
                        listener.onSendSuccess(s);
                    }
                }
            };
            socket = new Socket(IP, port);

            if (listener != null) {
                listener.onConnect();
            }
            transceiver.start(socket);
        } catch (IOException e) {
            if (listener != null) {
                listener.onConnectFalied();
            }
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket == null ? false : socket.isConnected();
    }

    public void disConnected() {
        if (transceiver != null) {
            transceiver.stop();
            transceiver = null;
        }
    }

    public SocketTransceiver getTransceiver() {
        return transceiver;
    }


    public interface TcpListener {

        void onConnect();

        void onConnectBreak();

        void onReceive(PreRspPojo preRspPojo);

        void onConnectFalied();

        void onSendSuccess(byte[] s);
    }

    public interface OnReceiveListener {
        void onReceive(String cmd, String[] jsonDatas);
    }
}

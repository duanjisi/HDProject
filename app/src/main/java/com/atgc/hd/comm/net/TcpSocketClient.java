/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.net;

import android.text.TextUtils;
import android.util.Log;

import com.atgc.hd.comm.ProtocolDecoder;

import java.io.IOException;
import java.net.Socket;

/**
 * <p>描述：
 * <p>作者：duanjisi 2018年 01月 17日
 */

public abstract class TcpSocketClient implements Runnable {
    private String IP;
    private int port;
    boolean connect = false;
    private SocketTransceiver transceiver;
    private Socket socket;
    private TcpListener listener;

    public void setListener(TcpListener listener) {
        this.listener = listener;
    }

    private static TcpSocketClient tcpSocketClient = null;

    public synchronized static TcpSocketClient getInstance() {
        if (tcpSocketClient == null) {
            tcpSocketClient = new TcpSocketClient() {
            };
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
                        if (!TextUtils.isEmpty(content)) {
                            if (listener != null) {
                                listener.onReceive(content);
                            }
                        }
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
                }
            };
            socket = new Socket(IP, port);
            connect = true;
            Log.d("connect", String.valueOf(connect));
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
        return connect;
    }

    public void disConnected() {
        connect = false;
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

        void onReceive(String s);

        void onConnectFalied();

        void onSendSuccess(byte[] s);
    }
}

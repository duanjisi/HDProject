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
import com.atgc.hd.comm.IPPort;
import com.atgc.hd.comm.ProtocolDecoder;
import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.protocol.ProtocolBody;
import com.atgc.hd.comm.protocol.ProtocolDecoder;
import com.atgc.hd.comm.utils.FileUtil;
import com.atgc.hd.comm.utils.StringUtils;
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
    private String port;
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

    public void connect(String IP, String port) {
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
                                preRspPojo.originJson = content;
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
            socket = new Socket(IP, Integer.valueOf(port));

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

    public void resetSocket() {
//        while (!isConnected()) {
//            try {
//                disConnected();
//                socket = new Socket(IPPort.getHOST(), Integer.valueOf(IPPort.getPORT()));
//                transceiver.start(socket);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        if (isConnected()) {
            disConnected();
        }
        connect(IPPort.getHOST(), IPPort.getPORT());
    }

    /**
     * 判断是否断开连接，断开返回true,没有返回false
     *
     * @param socket
     * @return
     */
    public Boolean isServerClose(Socket socket) {
        try {
            socket.sendUrgentData(0);//发送1个字节的紧急数据，默认情况下，服务器端没有开启紧急数据处理，不影响正常通信
            return false;
        } catch (Exception se) {
            return true;
        }
    }

    public SocketTransceiver getTransceiver() {
        return transceiver;
    public void sendMsg(byte[] bytes) {
        if (transceiver == null) {
            Logger.e("发送失败，transceiver == null");
        } else {
            transceiver.sendMSG(bytes);
        }
    }

    public void demoSendMsg(String cmd) {
        if (!Constants.isDemo) {
            return;
        }
        String demoResop = FileUtil.getAssets(cmd + "_req.txt");
        if (StringUtils.isEmpty(demoResop)) {
            return;
        } else {
            Logger.d("已找到配置文件：" + cmd + "_req.txt");
        }
        PreRspPojo preRspPojo = JSON.parseObject(demoResop, PreRspPojo.class);
        if (mapListener.containsKey(preRspPojo.Command)) {
            OnReceiveListener listener = mapListener.get(preRspPojo.Command);
            listener.onReceive(preRspPojo.Command, preRspPojo.Data);
        }
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

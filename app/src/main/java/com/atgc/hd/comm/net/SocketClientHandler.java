package com.atgc.hd.comm.net;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.Ip_Port;
import com.atgc.hd.comm.ProtocolDecoder;
import com.atgc.hd.comm.utils.DigitalUtils;
import com.orhanobut.logger.Logger;
import com.vilyever.socketclient.SocketClient;
//import com.vilyever.socketclient.SocketResponsePacket;

import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * 描述：socket通讯工具处理类
 * Created by duanjisi on 2018/1/15.
 */

public class SocketClientHandler {
    private static SocketClientHandler handler;
    private SocketClient socketClient;
    private SocketCallback socketCallback;

    public void setSocketCallback(SocketCallback socketCallback) {
        this.socketCallback = socketCallback;
    }

    public SocketClientHandler() {
//        socketClient = new SocketClient(Ip_Port.getHOST(), Ip_Port.getPORT());
        init();
    }

    public static SocketClientHandler getInstance() {
        if (handler == null) {
            handler = new SocketClientHandler();
        }
        return handler;
    }

    public void init() {
        if (this.socketClient != null) {
//            socketClient.setConnectionTimeout(1000 * 15);
//            socketClient.setHeartBeatInterval(1000 * 5);
//            socketClient.setRemoteNoReplyAliveTimeout(1000 * 60);
//            socketClient.setCharsetName("UTF-8");
//            socketClient.registerSocketDelegate(new SocketClient.SocketDelegate() {
//                @Override
//                public void onConnected(SocketClient client) {
////                    if (socketCallback != null) {
////                        socketCallback.onError("建立连接");
////                    }
//                    if (datas != null && datas.length != 0) {
//                        socketClient.send(datas);
//                        datas = null;
//                    }
//                }
//
//                @Override
//                public void onDisconnected(SocketClient client) {
//                    socketCallback.onError("断开连接");
//                }
//
//                @Override
//                public void onResponse(SocketClient client, @NonNull SocketResponsePacket responsePacket) {
//                    byte[] datas = responsePacket.getData();
//                    if (datas != null && datas.length != 0) {
//                        String content = ProtocolDecoder.parseContent(datas);
//                        if (!TextUtils.isEmpty(content) && socketCallback != null) {
//                            socketCallback.onResponse(content);
//                        }
//                    }
//                }
//            });
////            socketClient.connect();
        }
    }

    byte[] datas = null;

    public void sendMsg(String cmd, Map<String, String> map) {
//        if (socketClient != null) {
//            datas = DigitalUtils.getParamBytes(cmd, map);
//            if (socketClient.isConnected()) {
//                socketClient.send(datas);
//            } else {
//                new AsyncTask<Void, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        socketClient.connect();
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//                        super.onPostExecute(aVoid);
//                    }
//                }.execute();
//            }

//        if (socketClient != null) {
//            if (socketClient.isConnecting()) {
//                byte[] datas = DigitalUtils.getParamBytes(cmd, map);
//                socketClient.send(datas);
//            }
//            else {
//                new AsyncTask<Void, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        try {
//                            Thread.sleep(1000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        socketClient.connect();
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//                        super.onPostExecute(aVoid);
//                    }
//                }.execute();
//            }
//    }
    }

    public void sendHearBeatMsg() {
//        if (socketClient != null) {
//            datas = DigitalUtils.getParamBytes(DeviceCmd.HEART_BEAT, null);
//            if (socketClient.isConnected()) {
//                socketClient.send(datas);
//            } else {
//                new AsyncTask<Void, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        socketClient.connect();
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void aVoid) {
//                        super.onPostExecute(aVoid);
//                    }
//                }.execute();
//            }
//        }


//        byte[] bytes = DigitalUtils.getParamBytes(DeviceCmd.HEART_BEAT, null);
//        socketClient.setHeartBeatMessage(bytes);
    }

    public void disconnect() {
        if (socketClient != null && socketClient.isConnected()) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    socketClient.disconnect();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                }
            }.execute();
        }
    }

    public interface SocketCallback {
        void onError(String msg);

        void onResponse(String json);
    }
}

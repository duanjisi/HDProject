package com.atgc.hd.comm.net;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.atgc.hd.comm.Ip_Port;
import com.atgc.hd.comm.ProtocolDecoder;
import com.atgc.hd.comm.utils.DigitalUtils;
import com.orhanobut.logger.Logger;
import com.vilyever.socketclient.SocketClient;
import com.vilyever.socketclient.SocketResponsePacket;

import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

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
        socketClient = new SocketClient(Ip_Port.getHOST(), Ip_Port.getPORT());
    }

    public static SocketClientHandler getInstance() {
        if (handler == null) {
            handler = new SocketClientHandler();
        }
        return handler;
    }

    public void init() {
        if (this.socketClient != null) {
            socketClient.setConnectionTimeout(1000 * 15);
//            socketClient.setHeartBeatInterval(1000);
            socketClient.setRemoteNoReplyAliveTimeout(1000 * 60);
            socketClient.setCharsetName("UTF-8");
            socketClient.registerSocketDelegate(new SocketClient.SocketDelegate() {
                @Override
                public void onConnected(SocketClient client) {
                    if (socketCallback != null) {
                        socketCallback.onError("建立连接");
                    }
                }

                @Override
                public void onDisconnected(SocketClient client) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            try {
                                Thread.sleep(3 * 1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            socketClient.connect();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);

                        }
                    }.execute();
                }

                @Override
                public void onResponse(SocketClient client, @NonNull SocketResponsePacket responsePacket) {
                    byte[] datas = responsePacket.getData();
                    if (datas != null && datas.length != 0) {
                        String content = ProtocolDecoder.parseContent(datas);
                        if (!TextUtils.isEmpty(content) && socketCallback != null) {
                            socketCallback.onResponse(content);
                        }
                    }
                }
            });
            socketClient.connect();
        }
    }

    public void sendMsg(String cmd, Map<String, String> map) {
        if (socketClient != null && socketClient.isConnected()) {
            byte[] datas = DigitalUtils.getParamBytes(cmd, map);
            socketClient.send(datas);
        }
    }

    public interface SocketCallback {
        void onError(String msg);

        void onResponse(String json);
    }
}

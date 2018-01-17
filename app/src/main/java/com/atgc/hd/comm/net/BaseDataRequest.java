package com.atgc.hd.comm.net;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.atgc.hd.comm.Ip_Port;
import com.atgc.hd.comm.utils.DigitalUtils;

import net.jodah.typetools.TypeResolver;

import java.util.Map;

/**
 * Created by duanjisi on 2018/1/15.
 */

public abstract class BaseDataRequest<T> implements TcpSocketClient.TcpListener {
    private static String TAG = "BaseDataModel";
    private final Class<T> mGenericPojoClazz;
    private RequestCallback callback;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private TcpSocketClient tcpSocketClient;

    protected BaseDataRequest() {
        mGenericPojoClazz = (Class<T>) TypeResolver.resolveRawArgument(BaseDataRequest.class, getClass());
        tcpSocketClient = TcpSocketClient.getInstance();
        tcpSocketClient.setListener(this);
        if (!tcpSocketClient.isConnected()) {
            tcpSocketClient.connect(Ip_Port.getHOST(), Ip_Port.getPORT());
        }
    }

    public void send(final RequestCallback callback) {
        this.callback = callback;
        byte[] bytes = DigitalUtils.getBytes(getCommand(), getParams());
        tcpSocketClient.getTransceiver().sendMSG(bytes);
    }

    protected abstract boolean isParse();

    protected abstract Map<String, String> getParams();

    protected abstract String getCommand();


    @Override
    public void onConnect() {

    }

    @Override
    public void onConnectBreak() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure("连接断开");
            }
        });
    }

    @Override
    public void onConnectFalied() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure("连接失败");
            }
        });
    }

    @Override
    public void onReceive(String json) {
        PreRspPojo preRspPojo = null;
        preRspPojo = JSON.parseObject(json, PreRspPojo.class);
        if (preRspPojo.Result.equals("0")) {
            final T retT;
            if (isParse()) {
                retT = JSON.parseObject(preRspPojo.Data, mGenericPojoClazz);
            } else {
                retT = JSON.parseObject(json, mGenericPojoClazz);
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(retT);
                }
            });
        } else {
            final PreRspPojo finalPreRspPojo = preRspPojo;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onFailure(finalPreRspPojo.ErrorMessage);
                }
            });
        }
    }

    @Override
    public void onSendSuccess(byte[] s) {

    }

    public interface RequestCallback<T> {
        void onSuccess(T pojo);

        void onFailure(String msg);
    }
}

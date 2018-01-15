package com.atgc.hd.comm.net;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;

import net.jodah.typetools.TypeResolver;

import java.util.Map;

/**
 * Created by duanjisi on 2018/1/15.
 */

public abstract class BaseDataRequest<T> implements SocketClientHandler.SocketCallback {
    private static String TAG = "BaseDataModel";
    private final Class<T> mGenericPojoClazz;
    private SocketClientHandler socketClientHandler;
    private RequestCallback callback;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    protected BaseDataRequest() {
        mGenericPojoClazz = (Class<T>) TypeResolver.resolveRawArgument(BaseDataRequest.class, getClass());
        socketClientHandler = SocketClientHandler.getInstance();
        socketClientHandler.init();
        socketClientHandler.setSocketCallback(this);
    }

    public void send(final RequestCallback callback) {
        this.callback = callback;
        socketClientHandler.sendMsg(getCommand(), getParams());
    }

    protected abstract boolean isParse();

    protected abstract Map<String, String> getParams();

    protected abstract String getCommand();

    @Override
    public void onError(String msg) {
        if (callback != null) {
            callback.onFailure(msg);
        }
    }

    @Override
    public void onResponse(String json) {
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

    public interface RequestCallback<T> {
        void onSuccess(T pojo);

        void onFailure(String msg);
    }
}

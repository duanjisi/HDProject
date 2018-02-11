package com.atgc.hd.comm.net;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.IPPort;
import com.hdsocket.utils.DigitalUtils;

import net.jodah.typetools.TypeResolver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    protected Object[] mParams;
    private String mTag;

    protected BaseDataRequest() {
        this("");
    }

    protected BaseDataRequest(String tag, Object... params) {
        mParams = params;
        mGenericPojoClazz = (Class<T>) TypeResolver.resolveRawArgument(BaseDataRequest.class, getClass());
        tcpSocketClient = TcpSocketClient.getInstance();
        tcpSocketClient.setListener(this);
        if (!tcpSocketClient.isConnected()) {
            tcpSocketClient.connect(IPPort.getHOST(), IPPort.getPORT());
        }
    }

    public void send(final RequestCallback callback) {
        if (Constants.isDemo) {
            this.callback = callback;
            tcpSocketClient.demoSendMsg(getCommand());

        } else {
            this.callback = callback;
            byte[] bytes = DigitalUtils.getBytes(getCommand(), getParams());
            tcpSocketClient.sendMsg(bytes);
        }
    }

    protected abstract boolean isParse();

    protected abstract Map<String, String> getParams();

    protected abstract String getCommand();


    @Override
    public void onConnect() {

    }

    @Override
    public void onConnectBreak() {
        if (callback == null) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure("连接断开");
            }
        });
    }

    @Override
    public void onConnectFalied() {
        if (callback == null) {
            return;
        }

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure("连接失败");
            }
        });
    }

    @Override
    public void onReceive(PreRspPojo preRspPojo) {

        if ("0".equals(preRspPojo.Result)) {
            final T retT;
            if (isParse()) {
                retT = JSON.parseObject(preRspPojo.Data[0], mGenericPojoClazz);
            } else {
                retT = JSON.parseObject(preRspPojo.originJson, mGenericPojoClazz);
            }

            if (callback == null) {
                return;
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(retT);
                }
            });
        } else {
            final PreRspPojo finalPreRspPojo = preRspPojo;
            if (callback == null) {
                return;
            }
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

    public String jsonData() {
        String jsonMsg = "";
        JSONObject object = new JSONObject();
        try {
            object.put("Command", getCommand());

            JSONArray array = new JSONArray();
            array.put(new JSONObject(getParams()));

            object.put("Data", array);

            jsonMsg = object.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonMsg;
    }

    public interface RequestCallback<T> {
        void onSuccess(T pojo);

        void onFailure(String msg);
    }
}

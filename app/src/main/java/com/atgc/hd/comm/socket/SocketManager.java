package com.atgc.hd.comm.socket;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.atgc.hd.comm.net.request.PulseRequest;
import com.atgc.hd.comm.utils.StringUtils;
import com.hdsocket.net.header.HeaderResponse;
import com.hdsocket.net.request.BaseRequest;
import com.hdsocket.net.request.SendableBase;
import com.hdsocket.net.request.SendablePulse;
import com.xuhao.android.libsocket.impl.PulseManager;
import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.OkSocket;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.SocketActionAdapter;
import com.xuhao.android.libsocket.sdk.bean.IPulseSendable;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.bean.OriginalData;
import com.xuhao.android.libsocket.sdk.connection.IConnectionManager;
import com.xuhao.android.libsocket.sdk.protocol.IHeaderProtocol;

import java.nio.ByteOrder;
import java.util.Timer;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/2/6
 */
public class SocketManager {
    private static final String HOST = "172.16.10.127";
    private static final int PORT = 20001;

    // 心跳包数据
    private SendablePulse mPulseData;

    private IConnectionManager connectionManager;

    private static SocketManager socketManager = new SocketManager();

    private SocketManager() {
        mPulseData = new SendablePulse(new PulseRequest());
    }

    public static SocketManager intance() {
        return socketManager;
    }

    public void onDestory() {
        connectionManager.disConnect();
    }

    private Timer timer;

    /**
     * 在application初始化,只能初始化一次，多进程时需要区分主进程.
     *
     * @param application
     */
    public void initialize(Application application) {
        OkSocket.initialize(application, true);
    }

    public void initConfiguration() {
        timer = new Timer();

        //连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
        ConnectionInfo info = new ConnectionInfo(HOST, PORT);

        //调用OkSocket,开启这次连接的通道,拿到通道Manager
        connectionManager = OkSocket.open(info);

        //注册Socket行为监听器,SocketActionAdapter是回调的Simple类,其他回调方法请参阅类文档
        connectionManager.registerReceiver(socketActionAdapter());

        //根据已有的参配对象，建造一个新的参配对象并且付给通道管理器
        OkSocketOptions defaultOption = connectionManager.getOption();
        OkSocketOptions newOption = okSocketOptions(defaultOption);
        connectionManager.option(newOption);

        //调用管理器进行连接
        connectionManager.connect();
    }

    private OkSocketOptions okSocketOptions(OkSocketOptions defaultOption) {
        // 基于当前参配对象构建一个参配建造者类
        OkSocketOptions.Builder builder = new OkSocketOptions.Builder(defaultOption);
        // 设置接收到的数据包的包头
        builder.setHeaderProtocol(headerProtocol());
        // ========修改参数设置========
        // 心跳包间隔数
        builder.setPulseFrequency(60 * 1000);
        // 设置发送单个数据包的大小（默认50）
        builder.setSinglePackageBytes(1000);

        // 设置自定义的重连管理器
        builder.setReconnectionManager(new ReconnectManager());

        return builder.build();
    }

    private IHeaderProtocol headerProtocol() {
        return new IHeaderProtocol() {
            @Override
            public int getHeaderLength() {
                // 响应包的包头长度
                return 57;
            }

            @Override
            public int getBodyLength(byte[] header, ByteOrder byteOrder) {
                HeaderResponse headerResponse = new HeaderResponse();
                headerResponse.parse(header);
                return headerResponse.contentLength;
            }
        };
    }

    private SocketActionAdapter socketActionAdapter() {
        return new SocketActionAdapter() {
            /**
             * Socket通讯IO线程的启动<br>
             * 该方法调用后IO线程将会正常工作
             *
             * @param context
             * @param action
             */
            @Override
            public void onSocketIOThreadStart(Context context, String action) {
                super.onSocketIOThreadStart(context, action);
            }

            /**
             * Socket通讯IO线程的关闭<br>
             * 该方法调用后IO线程将彻底死亡
             *
             * @param context
             * @param action
             * @param e 关闭时将会产生的异常,IO线程一般情况下都会有异常产生
             */
            @Override
            public void onSocketIOThreadShutdown(Context context, String action, Exception e) {
                super.onSocketIOThreadShutdown(context, action, e);
            }

            /**
             * Socket断开后进行的回调<br>
             * 当Socket彻底断开后,系统会回调该方法
             *
             * @param context
             * @param info 这次连接的连接信息
             * @param action
             * @param e Socket断开时的异常信息,如果是正常断开(调用disconnect()),异常信息将为null.使用e变量时应该进行判空操作
             */
            @Override
            public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {
                super.onSocketDisconnection(context, info, action, e);
            }

            /**
             * 当Socket连接建立成功后<br>
             * 系统会回调该方法,此时有可能读写线程还未启动完成,不过不会影响大碍<br>
             * 当回调此方法后,我们可以认为Socket连接已经建立完成,并且读写线程也初始化完
             *
             * @param context
             * @param info 这次连接的连接信息
             * @param action
             */
            @Override
            public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
                super.onSocketConnectionSuccess(context, info, action);
                Log.e("socketManager", "onSocketConnectionSuccess 连接成功");
            }

            /**
             * 当Socket连接失败时会进行回调<br>
             * 建立Socket连接,如果服务器出现故障,网络出现异常都将导致该方法被回调<br>
             * 系统回调此方法时,IO线程均未启动.如果IO线程启动将会回调{@link #onSocketDisconnection(Context, ConnectionInfo, String, Exception)}
             *
             * @param context
             * @param info 这次连接的连接信息
             * @param action
             * @param e 连接未成功建立的错误原因
             */
            @Override
            public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
                super.onSocketConnectionFailed(context, info, action, e);
                Log.e("socketManager", "onSocketConnectionFailed 连接失败");

            }

            /**
             * Socket通讯读取到消息后的响应
             *
             * @param context
             * @param action
             * @param data 原始的读取到的数据{@link OriginalData}
             */
            @Override
            public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {
                super.onSocketReadResponse(context, info, action, data);

                analysisManager.onReceiveResponse(data);
            }

            /**
             * Socket通讯写出后的响应回调
             *
             * @param context
             * @param action
             * @param data 写出的数据{@link ISendable}
             */
            @Override
            public void onSocketWriteResponse(Context context, ConnectionInfo info, String action, ISendable data) {
                super.onSocketWriteResponse(context, info, action, data);
                // action的值参考 IAction 类
                Log.e("socketManager", "onSocketWriteResponse action：" + action);
            }

            /**
             * Socket心跳发送后的回调<br>
             * 心跳发送是一个很特殊的写操作<br>
             * 该心跳发送后将不会回调{@link #onSocketWriteResponse(Context, ConnectionInfo, String, ISendable)}方法
             *
             * @param context
             * @param info 这次连接的连接信息
             * @param data 心跳发送数据{@link IPulseSendable}
             */
            @Override
            public void onPulseSend(Context context, ConnectionInfo info, IPulseSendable data) {
                super.onPulseSend(context, info, data);
                Log.e("socketManager", "onPulseSend 心跳包发送");
            }
        };
    }

    private void registerDevice() {

        registertOnActionListener("", new OnActionAdapter(){
            @Override
            public void onSendSucess(String cmd, String serialNum) {
                startPulse();
                uploadGps();

                unRegistertOnActionListener(cmd);
            }

            @Override
            public void onSendFail(String cmd, String serialNum, String errorCode, String errorMsg) {
            }
        });
    }

    private void startPulse() {
        if (connectionManager != null) {
            Log.e("socketManager", "onSocketConnectionSuccess 开始心跳");
            PulseManager pulseManager = connectionManager.getPulseManager();

            //给心跳管理器设置心跳数据,一个连接只有一个心跳管理器,因此数据只用设置一次,如果断开请再次设置.
            pulseManager.setPulseSendable(mPulseData);

            //开始心跳,开始心跳后,心跳管理器会自动进行心跳触发
            pulseManager.pulse();
        }
    }

    private void uploadGps() {

    }

    AnalysisManager analysisManager = new AnalysisManager();

    /**
     * 发送请求
     *
     * @param object        请求数据
     * @return
     */
    public String launch(BaseRequest object) {
        if (connectionManager == null) {
            return "";
        }

        analysisManager.setResponseClass(object.getResponseCommand(), object.getResponseClass());

        if (object.serialNum == null || object.serialNum.length() == 0) {
            object.serialNum = StringUtils.getRandomString(20);
        }

        connectionManager.send(new SendableBase(object));

        return object.serialNum;
    }

    public void registertOnActionListener(String cmd, OnActionListener listener) {
        analysisManager.registertOnActionListener(cmd, listener);
    }

    public void unRegistertOnActionListener(String cmd) {
        analysisManager.unRegistertOnActionListener(cmd);
    }
}

/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.comm.socket;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.net.request.PulseRequest;
import com.atgc.hd.comm.net.request.base.BaseRequest;
import com.atgc.hd.comm.net.request.base.SendableBase;
import com.atgc.hd.comm.net.request.base.SendablePulse;
import com.atgc.hd.comm.net.response.base.HeaderResponse;
import com.atgc.hd.comm.utils.FileUtil;
import com.atgc.hd.comm.utils.StringUtils;
import com.orhanobut.logger.Logger;
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

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/2/6
 */
public class SocketTestManager {

    // 心跳包数据
    private SendablePulse mPulseData;

    private IConnectionManager connectionManager;

    private static SocketTestManager socketManager = new SocketTestManager();

    private AnalysisManager analysisManager = new AnalysisManager();

    private SocketTestManager() {
        mPulseData = new SendablePulse(new PulseRequest());
    }

    public static SocketTestManager intance() {
        return socketManager;
    }

    public void onDestory() {
        connectionManager.disConnect();
    }

    /**
     * 在application初始化,只能初始化一次，多进程时需要区分主进程.
     *
     * @param application
     */
    public void initialize(Application application) {
        OkSocket.initialize(application, true);
    }

    public void initConfiguration(String HOST, int PORT) {

        //连接参数设置(IP,端口号),这也是一个连接的唯一标识,不同连接,该参数中的两个值至少有其一不一样
        ConnectionInfo info = new ConnectionInfo(HOST, PORT);

        //调用OkSocket,开启这次连接的通道,拿到通道Manager
        connectionManager = OkSocket.open(info);

        //根据已有的参配对象，建造一个新的参配对象并且付给通道管理器
        OkSocketOptions defaultOption = connectionManager.getOption();
        OkSocketOptions newOption = okSocketOptions(defaultOption);
        connectionManager.option(newOption);

    }

    public void test(SocketActionAdapter adapter) {
//注册Socket行为监听器,SocketActionAdapter是回调的Simple类,其他回调方法请参阅类文档
        connectionManager.registerReceiver(adapter);

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
        builder.setSinglePackageBytes(500);

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

}

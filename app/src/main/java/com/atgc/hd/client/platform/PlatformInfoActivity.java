/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.client.platform;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.atgc.hd.R;
import com.atgc.hd.adapter.PlatformInfoAdapter;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.IPPort;
import com.atgc.hd.comm.net.TcpSocketClient;
import com.atgc.hd.comm.net.response.base.Response;
import com.atgc.hd.comm.socket.OnActionAdapter;
import com.atgc.hd.comm.socket.SocketManager;
import com.atgc.hd.db.dao.PlatformInfoDao;
import com.atgc.hd.entity.ActionEntity;
import com.atgc.hd.entity.PatInfo;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * <p>描述：平台消息
 * <p>作者：duanjisi 2018年 02月 05日
 */

public class PlatformInfoActivity extends BaseActivity {
    @BindView(R.id.listview)
    ListView listview;
    private PlatformInfoAdapter adapter;
    //    private TcpSocketClient tcpSocketClient = null;
    private PlatformInfoDao infoDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_list);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        barHelper.setTitleColor(getResources().getColor(R.color.white));
        infoDao = PlatformInfoDao.getInstance();
        initListView();
//        registerOnReceiveListener();
    }

    @Override
    public String toolBarTitle() {
        return getString(R.string.platform_msg);
    }

    private void initListView() {
        adapter = new PlatformInfoAdapter(context);
        listview.setAdapter(adapter);
        ArrayList<PatInfo> infos = (ArrayList<PatInfo>) infoDao.query();
        if (infos != null && infos.size() != 0) {
            adapter.initData(infos);
        }
    }

//    private void registerListener() {
//        tcpSocketClient = TcpSocketClient.getInstance();
//        if (!tcpSocketClient.isConnected()) {
//            tcpSocketClient.connect(IPPort.getHOST(), IPPort.getPORT());
//        }
//        tcpSocketClient.registerOnReceiveListener(new TcpSocketClient.OnReceiveListener() {
//            @Override
//            public void onReceive(String cmd, final String[] jsonData) {
//                Logger.json("下发文本消息", jsonData[0]);
//                // 注销监听
//                tcpSocketClient.unregisterOnReceiveListener(DeviceCmd.PAT_SEND_MESSAGE);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        PatInfo platforms = JSON.parseObject(jsonData[0], PatInfo.class);
//                        bindDatas(platforms);
//                    }
//                });
//            }
//        }, DeviceCmd.PAT_SEND_MESSAGE);
//    }

    /**
     * 注册监听网关发送巡更报文
     */
    private void registerOnReceiveListener() {
        SocketManager.intance().registertOnActionListener(requestGroupTag, DeviceCmd.PAT_SEND_MESSAGE, PatInfo.class, new OnActionAdapter() {
            @Override
            public void onResponseSuccess(String cmd, String serialNum, Response response, Bundle bundle) {

                List<PatInfo> patInfos = response.dataArray;
                final PatInfo patInfo = patInfos.get(0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bindDatas(patInfo);
                    }
                });
            }

            @Override
            public void onResponseFaile(String cmd, String serialNum, String errorCode, String errorMsg, Bundle bundle) {

            }
        });
    }

    private void bindDatas(PatInfo platform) {
        if (platform != null) {
            infoDao.save(platform);
            adapter.addData(platform);
        }
    }

    @Subscribe
    public void onMessageEvent(ActionEntity event) {
        if (event != null) {
            String action = event.getAction();
            if (action.equals(Constants.Action.PLATFORM_INFO)) {
                try {
                    Thread.sleep(500);
                    ArrayList<PatInfo> infos = (ArrayList<PatInfo>) infoDao.query();
                    if (infos != null && infos.size() != 0) {
                        adapter.initData(infos);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

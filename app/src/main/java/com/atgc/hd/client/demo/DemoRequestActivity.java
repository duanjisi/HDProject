package com.atgc.hd.client.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.base.adapter.ViewHolder;
import com.atgc.hd.base.adapter.interfaces.OnItemClickListener;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.IPPort;
import com.atgc.hd.comm.net.BaseDataRequest;
import com.atgc.hd.comm.net.TcpSocketClient;
import com.atgc.hd.comm.net.request.GPSRequest;
import com.atgc.hd.comm.net.request.GetTaskRequest;
import com.atgc.hd.comm.net.request.RegisterRequest;
import com.atgc.hd.comm.net.request.ReportPointStatusRequest;
import com.atgc.hd.comm.net.request.ReportTaskStatusRequest;
import com.atgc.hd.comm.widget.NiftyDialog;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <p>描述：简单的接口请求界面
 * <p>作者：liangguokui 2018/1/31
 */
public class DemoRequestActivity extends BaseActivity implements OnItemClickListener<String>, TcpSocketClient.OnReceiveListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    DemoRequestAdapter demoRequestAdapter;

    private TcpSocketClient tcpSocketClient = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_request);
        ButterKnife.bind(this);

        init();

        initView();
    }

    private void init() {
        tcpSocketClient = TcpSocketClient.getInstance();
        if (!tcpSocketClient.isConnected()) {
            tcpSocketClient.connect(IPPort.getHOST(), IPPort.getPORT());
        }

        tcpSocketClient.registerOnReceiveListener(this, DeviceCmd.PAT_SEND_TASK);
    }

    private void initView() {

        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        demoRequestAdapter = new DemoRequestAdapter(this, false);
        recyclerView.setAdapter(demoRequestAdapter);

        demoRequestAdapter.setOnItemClickListener(this);

        List<String> data = new ArrayList<>();
        data.add("设备注册");
        data.add("GPS坐标上传");
        data.add("获取巡更任务");
        data.add("上报巡查点结果");
        data.add("上报巡查任务状态");

        demoRequestAdapter.setNewData(data);
    }

    @Override
    public void onItemClick(ViewHolder viewHolder, String data, int position) {
        if (position == 0) {
            registerDevice();
        } else if (position == 1) {
            uploadGps();
        } else if (position == 2) {
            requestTaskList();
        } else if (position == 3) {
            reportPointStatus();
        } else if (position == 4) {
            reportTastStatus();
        }
    }

    @Override
    public void onReceive(String cmd, String[] jsonDatas) {
        Logger.json("巡更任务", jsonDatas[0]);
        if (DeviceCmd.PAT_SEND_TASK.equals(cmd)) {
            String msg = niftyJson(jsonDatas[0]);
            showMsg(msg);
        }
    }

    private void registerDevice() {
        final RegisterRequest request = new RegisterRequest();
        String jsonData = request.jsonData();

        launchDialog("设备注册", jsonData, "发送", request, new BaseDataRequest.RequestCallback() {
            @Override
            public void onSuccess(Object pojo) {
                showMsg("注册成功");
            }

            @Override
            public void onFailure(String msg) {
                showMsg("注册失败：" + msg);
            }
        });
    }

    private void uploadGps() {
        GPSRequest gpsRequest = new GPSRequest();
        gpsRequest.setLongitude("113.620759");
        gpsRequest.setLatitude("23.305057");
        String jsonData = gpsRequest.jsonData();

        launchDialog("GPS上传", jsonData, "发送", gpsRequest, new BaseDataRequest.RequestCallback() {
            @Override
            public void onSuccess(Object pojo) {
                showMsg("上传成功");
            }

            @Override
            public void onFailure(String msg) {
                showMsg("上传失败：" + msg);
            }
        });
    }

    private void requestTaskList() {
        GetTaskRequest taskRequest = new GetTaskRequest();
        String deviceID = "10012017020000000000";
        taskRequest.setDeviceID(deviceID);
        String jsonData = taskRequest.jsonData();

        launchDialog("获取巡更任务", jsonData, "发送", taskRequest, new BaseDataRequest.RequestCallback() {
            @Override
            public void onSuccess(Object pojo) {
                showMsg("发送成功");
            }

            @Override
            public void onFailure(String msg) {
                showMsg("发送失败：" + msg);
            }
        });

    }

    private void reportPointStatus() {
        ReportPointStatusRequest request = new ReportPointStatusRequest();
        request.setDeviceID("10012017f6d0101be5ed");

        request.setTaskID("95eba80212e04bc8aadfa3715bafb3c9");
        request.setTaskPointID("55b152d4a294491590858f70bc460fd8");
        request.setPointTime("2018-2-1 17:16:21");
        request.setPlanTime("2018-2-1 17:17:26");
        request.setHistoryPointStatus("2");
        String jsonData = request.jsonData();

        launchDialog("上报巡查点结果", jsonData, "发送", request, new BaseDataRequest.RequestCallback() {
            @Override
            public void onSuccess(Object pojo) {
                showMsg("发送成功");
            }

            @Override
            public void onFailure(String msg) {
                showMsg("发送失败：" + msg);
            }
        });
    }

    private void reportTastStatus() {
        ReportTaskStatusRequest request = new ReportTaskStatusRequest();
        request.setDeviceID("10012017020000000000");
        request.setUserId("");
        request.setTaskID("95eba80212e04bc8aadfa3715bafb3c9");
        request.setTaskStatus("2");
        request.setCarryStatus("1");
        request.setAbnormalReason("处理紧急情况");
        String jsonData = request.jsonData();

        launchDialog("上报巡查任务状态", jsonData, "发送", request, new BaseDataRequest.RequestCallback() {
            @Override
            public void onSuccess(Object pojo) {
                showMsg("发送成功");
            }

            @Override
            public void onFailure(String msg) {
                showMsg("发送失败：" + msg);
            }
        });
    }
    private void launchDialog(final String title,
                              final String msg,
                              final String btnTitle,
                              final BaseDataRequest request,
                              final BaseDataRequest.RequestCallback callback) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NiftyDialog.create(DemoRequestActivity.this)
                        .withTitle(title)
                        .withMessage(msg)
                        .withButton1Text(btnTitle)
                        .setButton1Click(new NiftyDialog.OnClickActionListener() {
                            @Override
                            public void onClick(NiftyDialog dialog, View clickView) {
                                dialog.withProgress(true).withTitle("请稍候...");
                                request.send(callback);
                            }
                        }).show();
            }
        });

    }

    private void showMsg(String msg) {
        showMsg("提示", msg);
    }

    private void showMsg(final String title, final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                NiftyDialog.create(DemoRequestActivity.this)
                        .withProgress(false)
                        .withTitle(title)
                        .withMessage(msg)
                        .withButton1Text("确定")
                        .setButton1Click(new NiftyDialog.OnClickActionListener() {
                            @Override
                            public void onClick(NiftyDialog dialog, View clickView) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

    }

    private String niftyJson(String jsonData) {
        String message = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            message = jsonObject.toString(2);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return message;
    }
}
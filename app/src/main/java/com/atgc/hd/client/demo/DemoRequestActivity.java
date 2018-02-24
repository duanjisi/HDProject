package com.atgc.hd.client.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.base.adapter.ViewHolder;
import com.atgc.hd.base.adapter.interfaces.OnItemClickListener;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.IPPort;
import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.net.BaseDataRequest;
import com.atgc.hd.comm.net.TcpSocketClient;
import com.atgc.hd.comm.net.request.GPSRequest;
import com.atgc.hd.comm.net.request.GetTaskRequest;
import com.atgc.hd.comm.net.request.RegisterRequest;
import com.atgc.hd.comm.net.request.ReportPointStatusRequest;
import com.atgc.hd.comm.net.request.ReportTaskStatusRequest;
import com.atgc.hd.comm.utils.StringUtils;
import com.atgc.hd.comm.widget.NiftyDialog;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <p>描述：简单的接口请求界面
 * <p>作者：liangguokui 2018/1/31
 */
public class DemoRequestActivity extends BaseActivity implements OnItemClickListener<String>, TcpSocketClient.OnReceiveListener {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private DemoRequestAdapter demoRequestAdapter;

    private TcpSocketClient tcpSocketClient = null;

    private String deviceID = "10032017784f4586ee3f";
    //    private String deviceID = "10012017f6d0101be5ed";
//    private String deviceID = "10012017f6d0101be5ed";
//    b35f71f73db14e34a0c3900050a3436c
//132654c184df4c348c9f51e06abd4e8a
//06d78f166cd84353b7a23176cb9eaff5
    private String taskID = "5e914e05308d4986b479f7d2ea61e879";
    private String userID = "a9abc433ae8d5281f60";

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

        tcpSocketClient.registerOnReceiveListener(this,
                DeviceCmd.PAT_SEND_TASK,
                DeviceCmd.HEART_BEAT,
                DeviceCmd.PAT_SEND_MESSAGE);
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

        demotest();
    }

    private Timer timer;
    private TimerTask timerTask;
    private String srcLongitude = "112.33";
    private String srcLatitude = "39.80";

    private void demotest() {
        timer = new Timer();
        final EditText edt = findViewById(R.id.editText);
        final TextView tvTips = findViewById(R.id.textView);

        edt.setText("10");
        tvTips.setText("设备ID：" + deviceID + "\n经纬度：" + srcLongitude + "    " + srcLatitude);

        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int period = Integer.valueOf(edt.getText().toString());

                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        String NLongitude = srcLongitude + getRandomString(4);
                        String NLatitude = srcLatitude + getRandomString(4);

                        uploadGps(NLongitude, NLatitude, true);

                        updateText(tvTips, "设备ID：" + deviceID + "\n经纬度：\n" + NLongitude + "\n" + NLatitude);
                    }
                };

                timer.schedule(timerTask, 500, period * 1000);
            }
        });

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerTask.cancel();
            }
        });
    }

    private void updateText(final TextView textView, final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(text);
            }
        });
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
            reportTaskStatus();
        }
    }

    @Override
    public void onReceive(String cmd, String[] jsonDatas) {
        Logger.json("巡更任务", jsonDatas[0]);
        if (DeviceCmd.PAT_SEND_TASK.equals(cmd)) {
            String msg = niftyJson(jsonDatas[0]);
            showMsg(msg);
        } else if (DeviceCmd.PAT_SEND_MESSAGE.equals(cmd)) {

        }
    }

    private void registerDevice() {
//        final RegisterRequest request = new RegisterRequest();
//        request.deviceID = DeviceParams.getInstance().getDeviceId();
//        String jsonData = request.jsonData();
//
//        launchDialog("设备注册", jsonData, "发送", request, new BaseDataRequest.RequestCallback() {
//            @Override
//            public void onSuccess(Object pojo) {
//                showMsg("注册成功");
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                showMsg("注册失败：" + msg);
//            }
//        });
    }

    private void uploadGps() {
        uploadGps("113.248159", "23.152168", false);
    }

    private void uploadGps(final String longitude, final String latitude, boolean now) {
//        GPSRequest gpsRequest = new GPSRequest();
//        gpsRequest.setDeviceID(deviceID);
//        gpsRequest.setTaskId(taskID);
//        gpsRequest.setUserID(userID);
//        gpsRequest.setLongitude(longitude);
//        gpsRequest.setLatitude(latitude);
//        String jsonData = gpsRequest.jsonData();
//
//        if (now) {
//            gpsRequest.send(new BaseDataRequest.RequestCallback() {
//                @Override
//                public void onSuccess(Object pojo) {
//                    Logger.e("上传成功：" + longitude + " " + latitude);
//                }
//
//                @Override
//                public void onFailure(String msg) {
//                    Logger.e("上传失败：" + longitude + " " + latitude);
//                }
//            });
//        } else {
//            launchDialog("GPS上传", jsonData, "发送", gpsRequest, new BaseDataRequest.RequestCallback() {
//                @Override
//                public void onSuccess(Object pojo) {
//                    showMsg("上传成功");
//                }
//
//                @Override
//                public void onFailure(String msg) {
//                    showMsg("上传失败：" + msg);
//                }
//            });
//        }
    }

    private void requestTaskList() {
//        GetTaskRequest taskRequest = new GetTaskRequest();
//        taskRequest.deviceID = deviceID;
//        String jsonData = taskRequest.jsonData();
//
//        launchDialog("获取巡更任务", jsonData, "发送", taskRequest, new BaseDataRequest.RequestCallback() {
//            @Override
//            public void onSuccess(Object pojo) {
//                showMsg("发送成功");
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                showMsg("发送失败：" + msg);
//            }
//        });

    }

    private void reportPointStatus() {
//        ReportPointStatusRequest request = new ReportPointStatusRequest();
//        request.setDeviceID(deviceID);
//
//        request.setTaskID(taskID);
//        request.setTaskPointID("2406077b13f14eafa26087aecabf0eba");
//
//        request.setPointTime("2018-2-2 16:33:08");
//        request.setPlanTime("2018-2-2 16:35:29");
//        request.setHistoryPointStatus("2");
//        String jsonData = request.jsonData();
//
//        launchDialog("上报巡查点结果", jsonData, "发送", request, new BaseDataRequest.RequestCallback() {
//            @Override
//            public void onSuccess(Object pojo) {
//                showMsg("发送成功");
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                showMsg("发送失败：" + msg);
//            }
//        });
    }

    private void reportTaskStatus() {
//        ReportTaskStatusRequest request = new ReportTaskStatusRequest();
//        request.setDeviceID(deviceID);
//        request.setUserId(userID);
//        request.setTaskID(taskID);
//        request.setTaskStatus("2");
//        request.setCarryStatus("0");
//        request.setAbnormalReason("处理紧急情况");
//        String jsonData = request.jsonData();
//
//        launchDialog("上报巡查任务状态", jsonData, "发送", request, new BaseDataRequest.RequestCallback() {
//            @Override
//            public void onSuccess(Object pojo) {
//                showMsg("发送成功");
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                showMsg("发送失败：" + msg);
//            }
//        });
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

    private static final String str = "0123456789";
    public static String getRandomString(int length) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(10);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
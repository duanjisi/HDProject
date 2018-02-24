package com.atgc.hd;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.PrefKey;
import com.atgc.hd.comm.net.BaseDataRequest;
import com.atgc.hd.comm.net.TcpSocketClient;
import com.atgc.hd.comm.net.request.GPSRequest;
import com.atgc.hd.comm.net.request.RegisterRequest;
import com.atgc.hd.comm.net.request.UploadEventRequest;
import com.atgc.hd.comm.utils.PreferenceUtils;
import com.orhanobut.logger.Logger;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>描述：
 * <p>作者：duanjisi 2018/1/16
 */
public class TestActivity extends BaseActivity {
    private final String Tag = TestActivity.class.getSimpleName();
    private TextView tv_msg;
    private Button btn, btn_beat, btn_test2;
    private EditText etHost, etPort;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btn = findViewById(R.id.btn_test);
        btn_beat = findViewById(R.id.btn_beat);
        btn_test2 = findViewById(R.id.btn_test2);
        etHost = findViewById(R.id.et_ip);
        etPort = findViewById(R.id.et_Port);
        tv_msg = findViewById(R.id.tv_msg);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testRegister();
            }
        });

        btn_beat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                testHearBeat();
                testGPS();
            }
        });

        btn_test2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                GetTaskRequest request = new GetTaskRequest();
//                request.setDeviceID("10012017f6d0101be5ed");
//                request.send(new BaseDataRequest.RequestCallback<String>() {
//                    @Override
//                    public void onSuccess(String pojo) {
//                        showToast(pojo);
//                    }
//
//                    @Override
//                    public void onFailure(String msg) {
//
//                    }
//                });
            }
        });

        TcpSocketClient.getInstance().registerOnReceiveListener(new TcpSocketClient.OnReceiveListener() {
            @Override
            public void onReceive(String cmd, final String[] jsonDatas) {
//                Logger.e("jsonData : " + jsonDatas);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        showToast("jsonData : " + jsonDatas);
                    }
                });
            }
        }, DeviceCmd.PAT_SEND_TASK);
    }

    private StringBuilder sb = new StringBuilder();

    private void testHearBeat() {
//        HeartBeatRequest request = new HeartBeatRequest();
//        request.send(new BaseDataRequest.RequestCallback<String>() {
//            @Override
//            public void onSuccess(String pojo) {
////                showToast(pojo);
//                println(pojo);
//            }
//
//            @Override
//            public void onFailure(String msg) {
////                showToast(msg);
//                println(msg);
//            }
//        });
    }

    private void println(String str) {
        sb.append(str + "\n");
        tv_msg.setText(sb.toString());
    }

    private void testGPS() {
//        GPSRequest gpsRequest = new GPSRequest();
//        gpsRequest.send(new BaseDataRequest.RequestCallback() {
//            @Override
//            public void onSuccess(Object pojo) {
//                Logger.e("发送成功：" + pojo.toString());
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                Logger.e("发送失败：" + msg);
//            }
//        });
    }

    private void testRegister() {
//        HashMap<String, String> map = new HashMap<>();
//        map.put("Type", "1");
//        map.put("deviceID", "10012017020000000000");
//        map.put("manufacturer", "XXX厂商");
//        map.put("macNO", "102");
//        map.put("locationAddr", "南门停车场入口");
//        map.put("name", "停车场设备");
//        map.put("ip", "172.16.10.22");
//        map.put("gateWay", "00000000000000000");
//        map.put("mac", "00:FF:81:99:2F");
//        map.put("mask", "255.255.255.0");
//        map.put("version", "V1.0.16_20171225001");
//        byte[] datas = DigitalUtils.getBytes(DeviceCmd.REGISTER, map);

        String host = getText(etHost);
        String port = getText(etPort);
        if (!TextUtils.isEmpty(host)) {
            if (isHostMatcher(host)) {
                PreferenceUtils.putString(context, PrefKey.HOST, host);
            } else {
                showToast("IP地址不合法");
                return;
            }
        } else {
            showToast("IP地址为空");
            return;
        }

        if (!TextUtils.isEmpty(port)) {
            if (isPortMatcher(port)) {
                PreferenceUtils.putInt(context, PrefKey.PORT, Integer.parseInt(port));
            } else {
                showToast("端口号不合法");
                return;
            }
        } else {
            showToast("端口为空");
            return;
        }

//        RegisterRequest request = new RegisterRequest();
//        request.send(new BaseDataRequest.RequestCallback<String>() {
//            @Override
//            public void onSuccess(String json) {
//                showToast(json);
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                showToast(msg);
//            }
//        });
    }

    private void upLoadEmergency() {
        HashMap<String, String> map = new HashMap<>();
        map.put("deviceId", "");
        map.put("longitude", "");
        map.put("latitude", "");
        map.put("uploadTime", "");
        map.put("description", "");
        map.put("picUrl", "");
        map.put("videoUrl", "");
        map.put("place", "");
        map.put("eventType", "");
        map.put("taskID", "");
        UploadEventRequest request = new UploadEventRequest(Tag, map);
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {

            }

            @Override
            public void onFailure(String msg) {
                showToast(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TcpSocketClient.getInstance().unregisterOnReceiveListener(DeviceCmd.PAT_SEND_TASK);
    }

    private boolean isHostMatcher(String str) {
        Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    private boolean isPortMatcher(String str) {
        Pattern pattern = Pattern.compile("^([1-9]|[1-9]\\\\d{1,3}|[1-6][0-5][0-5][0-3][0-5])$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * @param msg 内容
     * @return: void
     */
    protected void showToast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}

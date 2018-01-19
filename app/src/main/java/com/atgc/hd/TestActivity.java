package com.atgc.hd;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.comm.net.BaseDataRequest;
import com.atgc.hd.comm.net.request.HeartBeatRequest;
import com.atgc.hd.comm.net.request.RegisterRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>描述：
 * <p>作者：duanjisi 2018/1/16
 */
public class TestActivity extends BaseActivity {
    private Button btn, btn_beat;
    private EditText etHost, etPort;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btn = findViewById(R.id.btn_test);
        btn_beat = findViewById(R.id.btn_beat);
        etHost = findViewById(R.id.et_ip);
        etPort = findViewById(R.id.et_Port);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testRegister();
            }
        });
        btn_beat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testHearBeat();
            }
        });


    }

    private void testHearBeat() {
        HeartBeatRequest request = new HeartBeatRequest();
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String pojo) {
                showToast(pojo);
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg);
            }
        });
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

        } else {
            showToast("IP地址为空");
        }
        RegisterRequest request = new RegisterRequest();
        request.send(new BaseDataRequest.RequestCallback<String>() {
            @Override
            public void onSuccess(String json) {
                showToast(json);
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
    }

    private boolean isMatcher(String str) {
        Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
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

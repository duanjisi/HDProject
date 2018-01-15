package com.atgc.hd;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.atgc.hd.base.BaseActivity;

import java.util.HashMap;

/**
 * Created by duanjisi on 2018/1/15.
 */

public class TestActivity extends BaseActivity {
    private Button btn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btn = findViewById(R.id.btn_test);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testNet();
            }
        });
    }

    private void testNet() {
        HashMap<String, String> map = new HashMap<>();
//        map.put("StartTime", "2017-11-10");
//        map.put("EndTime", "2017-11-10");
//        map.put("UserType", "MONTH_A");
//        map.put("CredenceType", "CAR_PLATE");
//        map.put("CredenceNo", "湘 A123456");
//        map.put("UserName", "张三");
//        map.put("UserNo", "1006");
//        map.put("OpTime", "2017-11-10 08:00:01");


        map.put("Type", "1");
        map.put("deviceID", "10012017020000000000");
        map.put("manufacturer", "XXX厂商");
        map.put("macNO", "102");
        map.put("locationAddr", "南门停车场入口");
        map.put("name", "停车场设备");
        map.put("ip", "172.16.10.22");
        map.put("gateWay", "00000000000000000");
        map.put("mac", "00:FF:81:99:2F");
        map.put("mask", "255.255.255.0");
        map.put("version", "V1.0.16_20171225001");
        socketClientHandler.sendMsg("COM_DEV_REGISTER", map);
    }

    @Override
    public void onError(String msg) {
        showToast(msg);
    }

    @Override
    public void onResponse(String json) {
        showToast(json);
    }
}

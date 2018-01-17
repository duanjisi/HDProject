package com.atgc.hd;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.atgc.hd.comm.Utils;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.adapter.DiskLogAdapter;

public class MainActivity extends Activity {
    private TextView tv_net, tvResult;

    private WifiManager my_wifiManager;
    private WifiInfo wifiInfo;
    private DhcpInfo dhcpInfo;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        my_wifiManager = ((WifiManager) getSystemService("wifi"));
        dhcpInfo = my_wifiManager.getDhcpInfo();
        wifiInfo = my_wifiManager.getConnectionInfo();

        initViews();
    }

    private void initViews() {
        tvResult = findViewById(R.id.tv_net);
        tv_net = findViewById(R.id.tv_start);
        tv_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TestActivity.class));
            }
        });
        Utils.printIpAddress();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}

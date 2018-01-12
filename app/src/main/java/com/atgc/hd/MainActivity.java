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

        Log.println(4, "hahawtf", "let me see.......................................................");

        Logger.v("hahha", "let me see...............");
        Logger.d("hahha", "let me see...............");
        Logger.i("hahha", "let me see...............");
        Logger.w("hahha", "let me see...............");
        Logger.e("hahha", "let me see...............");


        Logger.addLogAdapter(new DiskLogAdapter());

        my_wifiManager = ((WifiManager) getSystemService("wifi"));
        dhcpInfo = my_wifiManager.getDhcpInfo();
        wifiInfo = my_wifiManager.getConnectionInfo();

        initViews();

        throw new NullPointerException("发生空指针异常啦。。。。");
    }

    private void initViews() {
        tvResult = findViewById(R.id.tv_net);
        tv_net = findViewById(R.id.tv_start);
        tv_net.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
        Utils.printIpAddress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StringBuilder sb = new StringBuilder();
        sb.append("网络信息：");
//        sb.append("\nipAddress：" + Utils.getLocalInetAddress().toString());
//        sb.append("\nMac：" + Utils.getLocalMacAddressFromIp(MainActivity.this));
//        sb.append("\nGateWay：" + Utils.getGateway());
        sb.append("\nipAddress：" + intToIp(dhcpInfo.ipAddress));
        sb.append("\nnetmask：" + intToIp(dhcpInfo.netmask));
        sb.append("\ngateway：" + intToIp(dhcpInfo.gateway));
        sb.append("\nserverAddress：" + intToIp(dhcpInfo.serverAddress));
        sb.append("\ndns1：" + intToIp(dhcpInfo.dns1));
        sb.append("\ndns2：" + intToIp(dhcpInfo.dns2));
        sb.append("\n");
//        System.out.println(intToIp(dhcpInfo.ipAddress));
//        System.out.println(intToIp(dhcpInfo.netmask));
//        System.out.println(intToIp(dhcpInfo.gateway));
//        System.out.println(intToIp(dhcpInfo.serverAddress));
//        System.out.println(intToIp(dhcpInfo.dns1));
//        System.out.println(intToIp(dhcpInfo.dns2));
//        System.out.println(dhcpInfo.leaseDuration);

        sb.append("Wifi信息：");
        sb.append("\nIpAddress：" + intToIp(wifiInfo.getIpAddress()));
        sb.append("\nMacAddress：" + wifiInfo.getMacAddress());
        tvResult.setText(sb.toString());
    }

    private String intToIp(int paramInt) {

        return (paramInt & 0xFF) + "." + (0xFF & paramInt >> 8) + "." + (0xFF & paramInt >> 16) + "."
                + (0xFF & paramInt >> 24);
    }


}

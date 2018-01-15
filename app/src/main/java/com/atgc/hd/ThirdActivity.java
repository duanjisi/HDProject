package com.atgc.hd;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.atgc.hd.comm.net.ClientSocket;

/**
 * Created by duanjisi on 2018/1/11.
 */

public class ThirdActivity extends Activity {
    private TextView tvRegister, txt1;
    Handler handler;
    ClientSocket clientSocket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        initViews();
    }

    private void initViews() {
        tvRegister = findViewById(R.id.tv_device_register);
        txt1 = findViewById(R.id.tv_str);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123) {
                    txt1.setText("\n" + msg.obj.toString());
                }
            }
        };
        clientSocket = new ClientSocket(handler);
        // 客户端启动ClientThread线程创建网络连接、读取来自服务器的数据
        new Thread(clientSocket).start();
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clientSocket.sendMsg();
            }
        });
    }
}
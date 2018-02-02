package com.atgc.hd.client.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.atgc.hd.MainActivity;
import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.client.tasklist.TaskListActivity;
import com.atgc.hd.comm.service.DeviceBootService;
import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

/**
 * <p>描述：引导页面
 * <p>作者：liangguokui 2018/1/22
 */
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        barHelper.displayActionBar(false);
//        Intent i = new Intent(context, DeviceBootService.class);
//        context.startService(i);
        //TODO 检查服务是否已开启
        //TODO 校时功能
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1 * 5000);
    }
}

package com.atgc.hd.client.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.client.setting.SettingActivity;
import com.atgc.hd.client.tasklist.TaskListActivity;
import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.PrefKey;
import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.net.response.base.Response;
import com.atgc.hd.comm.socket.OnActionAdapter;
import com.atgc.hd.comm.socket.RegisterEntity;
import com.atgc.hd.comm.socket.SocketManager;
import com.atgc.hd.comm.utils.PreferenceUtils;

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

        barHelper.displayActionBar(false);


//        if (!Utils.isServiceRunning(context, DeviceBootService.class.getName())) {
//            Intent i = new Intent(context, DeviceBootService.class);
//            context.startService(i);
//        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
//                Constants.isDemo = true;
                if (Constants.isDemo) {
                    openActivity(TaskListActivity.class);
                    finish();
                } else {
                    registerDevice();
                }
            }
        }, 1 * 3000);
    }

    private void registerDevice() {
        registerOnActionListener(DeviceCmd.REGISTER, new OnActionAdapter() {
            @Override
            public void onResponseSuccess(String cmd, String serialNum, Response response) {
                super.onResponseSuccess(cmd, serialNum, response);
                openActivity(TaskListActivity.class);
                finish();
            }

            @Override
            public void onResponseFaile(String cmd, String serialNum, String errorCode, String errorMsg) {
                super.onResponseFaile(cmd, serialNum, errorCode, errorMsg);
            }
        });

        final RegisterEntity request = new RegisterEntity();

        request.deviceID = DeviceParams.getInstance().getDeviceId();

        SocketManager.intance().launch(request);
    }

    private void goNextPager() {
        boolean isSetted = PreferenceUtils.getBoolean(context, PrefKey.SETTED, false);
        if (!isSetted) {
            openActivity(SettingActivity.class);
        } else {
            openActivity(TaskListActivity.class);
        }
    }

}

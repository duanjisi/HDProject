package com.atgc.hd.client.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.client.setting.SettingActivity;
import com.atgc.hd.client.tasklist.TaskListActivity;
import com.atgc.hd.comm.Constants;
import com.atgc.hd.comm.DeviceCmd;
import com.atgc.hd.comm.IPPort;
import com.atgc.hd.comm.PrefKey;
import com.atgc.hd.comm.Utils;
import com.atgc.hd.comm.config.DeviceParams;
import com.atgc.hd.comm.net.request.GPSRequest;
import com.atgc.hd.comm.net.request.RegisterRequest;
import com.atgc.hd.comm.net.response.base.Response;
import com.atgc.hd.comm.service.DeviceBootService;
import com.atgc.hd.comm.socket.OnActionAdapter;
import com.atgc.hd.comm.socket.OnActionListener;
import com.atgc.hd.comm.socket.SocketManager;
import com.atgc.hd.comm.socket.SocketTestManager;
import com.atgc.hd.comm.utils.PreferenceUtils;
import com.atgc.hd.comm.utils.StringUtils;
import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;
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

        final LottieAnimationView mLottieLove = findViewById(R.id.loading_animation);
        mLottieLove.setAnimation("building.json");
        mLottieLove.loop(true);
        mLottieLove.playAnimation();

        barHelper.displayActionBar(false);
        if (!Utils.isServiceRunning(context, DeviceBootService.class.getName())) {
            Intent i = new Intent(context, DeviceBootService.class);
            context.startService(i);
        }

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

        TextView textView = findViewById(R.id.textView);
        textView.setText("请稍后，正在初始化...");
        textView.append("\n" + IPPort.getHOST());
        textView.append("\n" + IPPort.getPORT());

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocketManager.intance().onDestory();
                openActvityForResult(SettingActivity.class, 23);
            }
        });
    }

    private OnActionListener onActionListener = new OnActionAdapter() {
        @Override
        public void onResponseSuccess(String cmd, String serialNum, Response response, Bundle bundle) {
            SocketManager.intance().startPulse();
            if (!Utils.isServiceRunning(context, DeviceBootService.class.getName())) {
                Intent i = new Intent(context, DeviceBootService.class);
                context.startService(i);
            }
            openActivity(TaskListActivity.class);
            SplashActivity.this.finish();
                    }

        @Override
        public void onResponseFaile(String cmd, String serialNum, String errorCode, String errorMsg, Bundle bundle) {
        }
    };


    private void registerDevice() {
        SocketManager.intance().registertOnActionListener(requestGroupTag, DeviceCmd.REGISTER, null, onActionListener);

        RegisterRequest request = new RegisterRequest();

        request.deviceID = DeviceParams.getInstance().getDeviceId();

        SocketManager.intance().launch(requestGroupTag, request, null, true);
    }

    private void goNextPager() {
        boolean isSetted = PreferenceUtils.getBoolean(context, PrefKey.SETTED, false);
        if (!isSetted) {
            openActivity(SettingActivity.class);
        } else {
            openActivity(TaskListActivity.class);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            TextView textView = findViewById(R.id.textView);
            textView.setText("请稍后，正在初始化...");
            textView.append("\n" + IPPort.getHOST());
            textView.append("\n" + IPPort.getPORT());

            SocketManager.intance().onDestory();
            SocketManager.intance().connect(IPPort.getHOST(), IPPort.getPORT());
        }
    }
}

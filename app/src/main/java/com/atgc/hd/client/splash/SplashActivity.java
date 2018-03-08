package com.atgc.hd.client.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.client.setting.SettingActivity;
import com.atgc.hd.client.tasklist.TaskListActivity;
import com.atgc.hd.comm.IPPort;
import com.atgc.hd.comm.Utils;
import com.atgc.hd.comm.service.DeviceBootService;
import com.atgc.hd.comm.socket.SocketManager;
import com.atgc.hd.entity.EventMessage;
import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * <p>描述：引导页面
 * <p>作者：liangguokui 2018/1/22
 */
public class SplashActivity extends BaseActivity {
    @BindView(R.id.btn_retry)
    public Button btnRetry;

    @BindView(R.id.textView)
    public TextView tvTips;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        final LottieAnimationView mLottieLove = findViewById(R.id.loading_animation);
        mLottieLove.setAnimation("building.json");
        mLottieLove.loop(true);
        mLottieLove.playAnimation();

        barHelper.displayActionBar(false);
        if (Utils.isServiceRunning(context, DeviceBootService.class.getName())) {
            sendEventMessageDelay("check_socket_connect");
        } else {
            Intent i = new Intent(context, DeviceBootService.class);
            context.startService(i);
        }

        tvTips.setText("请稍后，正在初始化...");
        tvTips.append("\n" + IPPort.getHOST());
        tvTips.append("\n" + IPPort.getPORT());

        EventBus.getDefault().register(this);
    }

    @OnClick(R.id.btn_retry)
    public void retryRegister() {
        btnRetry.setVisibility(View.INVISIBLE);
        tvTips.setText("请稍后，正在初始化...");
        tvTips.append("\n" + IPPort.getHOST());
        tvTips.append("\n" + IPPort.getPORT());
    }

    @OnClick(R.id.button)
    public void openNetSetting() {
        SocketManager.intance().onDestory();
        openActvityForResult(SettingActivity.class, 23);
    }

    @Subscribe
    public void readyToNextAty(EventMessage message) {
        if ("ready_to_next_aty".equals(message.eventTag)) {
            Logger.e("准备啦。。。");
            openActivity(TaskListActivity.class);
            SplashActivity.this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            TextView textView = findViewById(R.id.textView);
            textView.setText("请稍后，正在初始化...");
            textView.append("\n" + IPPort.getHOST());
            textView.append("\n" + IPPort.getPORT());

            SocketManager.intance().switchConnect();
        }
    }

    private void sendEventMessageDelay(final String eventTag) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendEventMessage(eventTag, null);
            }
        }, 2 * 1000);
    }

    private void sendEventMessage(String eventTag, Object object) {
        EventMessage msg = new EventMessage(eventTag, object);
        EventBus.getDefault().post(msg);
    }
}
package com.atgc.hd.client.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.client.setting.NetSettingActivity;
import com.atgc.hd.client.tasklist.TaskListActivity;
import com.atgc.hd.comm.Constants;
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
    @BindView(R.id.btn_register)
    public Button btnRetry;

    @BindView(R.id.btn_entrypt)
    public Button btnEntrypt;

    @BindView(R.id.textView)
    public TextView tvTips;

    @BindView(R.id.textView3)
    public TextView tvLog;

    @BindView(R.id.scrollView)
    public ScrollView scrollView;

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

        String btnText = Constants.isEntry ? "加密模式：ON" : "加密模式OFF";
        btnEntrypt.setText(btnText);

        tvTips.setText("\n" + IPPort.getHOST());
        tvTips.append("\n" + IPPort.getPORT());

        EventBus.getDefault().register(this);
    }

    @OnClick(R.id.btn_register)
    public void retryRegister() {
        //        Constants.isDemo = true;
        tvTips.setText("请稍后，正在初始化注册中...");
        if (Utils.isServiceRunning(context, DeviceBootService.class.getName())) {
            sendEventMessageDelay("check_socket_connect");
        } else {
            Intent i = new Intent(context, DeviceBootService.class);
            context.startService(i);
        }
    }

    @OnClick(R.id.btn_entrypt)
    public void clickEntrypt() {
        Constants.isEntry = !Constants.isEntry;
        String btnText = Constants.isEntry ? "加密模式：ON" : "加密模式OFF";
        btnEntrypt.setText(btnText);
    }

    @OnClick(R.id.btn_net_setting)
    public void openNetSetting() {
        SocketManager.intance().onDestory();
        openActvityForResult(NetSettingActivity.class, 23);
    }

    @Subscribe
    public void readyToNextAty(EventMessage message) {

        if ("ready_to_next_aty".equals(message.eventTag)) {
            Logger.e("准备啦。。。");
            tvLog.append("\n 3 秒后跳转...");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    openActivity(TaskListActivity.class);
                    SplashActivity.this.finish();
                }
            }, 3 * 1000);
        }
    }

    @Subscribe
    public void socketlog(final EventMessage message) {
        if (message.checkTag("socket_log")) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String log = (String) message.object;
                    tvLog.append("\n" + log);
                }
            });
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
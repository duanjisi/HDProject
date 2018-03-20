package com.atgc.hd.client.tasklist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.entity.EventMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * <p>描述：显示socket发送/接收的报文日志
 * <p>作者：liangguokui 2018/3/13
 */
public class NetLogActivity extends BaseActivity {

    @BindView(R.id.tv_log)
    public TextView tvLog;

    @BindView(R.id.scrollView)
    public ScrollView scrollView;

    @BindView(R.id.btn_gps)
    public Button btnGPS;

    @BindView(R.id.btn_stop)
    public Button btnLogSwitch;

    // 0-开启、1-关闭
    private String tagLog = "0";
    // 0-开启、1-关闭
    private String tagGpsLog = "1";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_log);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);
    }

    @Override
    public String toolBarTitle() {
        return "日志记录";
    }

    @OnClick(R.id.btn_stop)
    public void clickLogSwitch(View view) {
        if ("0".equals(tagLog)) {
            btnLogSwitch.setText("日志-OFF");
            tagLog = "1";
        } else {
            btnLogSwitch.setText("日志-ON");
            tagLog = "0";
        }

        updateStatus();
    }

    @OnClick(R.id.btn_clear)
    public void clickClean(View view) {
        tvLog.setText("");
        updateStatus("0");
    }

    @OnClick(R.id.btn_gps)
    public void clickGps(View view) {
        if ("0".equals(tagGpsLog)) {
            btnGPS.setText("GPS报文-OFF");
            tagGpsLog = "1";
        } else {
            btnGPS.setText("GPS报文-ON");
            tagGpsLog = "0";
        }

        updateStatus();
    }

    @Subscribe
    public void socketlog(final EventMessage message) {
        if (!message.checkTag("show_net_log")) {
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String log = (String) message.object;
                tvLog.setText(log);
            }
        });
    }

    private void updateStatus() {
        updateStatus("1");
    }

    /**
     *
     * @param tagClean 0-清除日志、1-不清除日志
     */
    private void updateStatus(String tagClean) {
        String status = tagLog + "##" + tagGpsLog + "##" + tagClean;
        EventBus.getDefault().post(new EventMessage("receive_switch_status", status));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

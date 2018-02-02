/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.TextView;
import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.comm.Constants;
import com.atgc.hd.entity.ActionEntity;
import com.atgc.hd.entity.EventEntity;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * <p>描述：上报状态页
 * <p>作者：duanjisi 2018年 01月 26日
 */

public class UploadStateActivity extends BaseActivity {
    private static final int DELAY_MILlIS = 1000;
    @BindView(R.id.tv_time)
    TextView tvTime;
    private int interval = 3;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (interval > 0) {
                        interval--;
                        tvTime.setText(interval + "秒后返回首页");
                        handler.sendEmptyMessageDelayed(0, DELAY_MILlIS);
                    } else {
//                        tvTime.setText("发送验证码");
//                        handler.removeMessages(0);
                        EventBus.getDefault().post(new ActionEntity(Constants.Action.EXIT_ACTIVITY));
                        setResult(100);
                        finish();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_state);
        ButterKnife.bind(this);
        tvTime.setText(interval + "秒后返回首页");
        barHelper.setTitleColor(getResources().getColor(R.color.white));
        handler.sendEmptyMessageDelayed(0, DELAY_MILlIS);
    }

    @Override
    public String toolBarTitle() {
        return getString(R.string.upload_success);
    }
}

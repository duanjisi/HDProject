
package com.atgc.hd.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.client.tasklist.TaskListActivity;
import com.atgc.hd.comm.PrefKey;
import com.atgc.hd.comm.net.TcpSocketClient;
import com.atgc.hd.comm.utils.PreferenceUtils;
import com.atgc.hd.widget.SuperEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * <p>描述：设置IP,端口
 * <p>作者：duanjisi 2018年 01月 25日
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.et_ip)
    SuperEditText etIp;
    @BindView(R.id.et_Port)
    EditText etPort;
    @BindView(R.id.et_image_ip)
    SuperEditText etImageIp;
    @BindView(R.id.et_image_Port)
    EditText etImagePort;
    @BindView(R.id.btn_next)
    Button btnNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        barHelper.setTitleColor(getResources().getColor(R.color.white));
        bindDatas();
    }


    private void bindDatas() {
        String ip = PreferenceUtils.getString(context, PrefKey.HOST, "");
        String port = PreferenceUtils.getString(context, PrefKey.PORT, "");
        String ip_image = PreferenceUtils.getString(context, PrefKey.HOST_IMAGE, "");
        String port_image = PreferenceUtils.getString(context, PrefKey.PORT_IMAGE, "");

        if (!TextUtils.isEmpty(ip)) {
            etIp.setText(ip);
        }
        if (!TextUtils.isEmpty(port)) {
            etPort.setText(port);
        }
        if (!TextUtils.isEmpty(ip_image)) {
            etImageIp.setText(ip_image);
        }
        if (!TextUtils.isEmpty(port_image)) {
            etImagePort.setText(port_image);
        }
    }

    @Override
    public String toolBarTitle() {
        return getString(R.string.setting);
    }

    @OnClick(R.id.btn_next)
    public void onViewClicked() {
        nextStep();
    }

    private void nextStep() {
        String host = etIp.getText().toString();
        String port = getText(etPort);

        String host_image = etImageIp.getText().toString();
        String port_image = etImagePort.getText().toString();

        if (!TextUtils.isEmpty(host)) {
            if (isHostMatcher(host)) {
                PreferenceUtils.putString(context, PrefKey.HOST, host);
            } else {
                showToast("IP地址不合法");
                return;
            }
        } else {
            showToast("IP地址为空");
            return;
        }
        if (!TextUtils.isEmpty(port)) {
            PreferenceUtils.putString(context, PrefKey.PORT, port);
//            if (isPortMatcher(port)) {
//                PreferenceUtils.putInt(context, PrefKey.PORT, Integer.parseInt(port));
//            } else {
//                showToast("端口号不合法");
//                return;
//            }
        } else {
            showToast("端口为空");
            return;
        }

        if (!TextUtils.isEmpty(host_image)) {
            PreferenceUtils.putString(context, PrefKey.HOST_IMAGE, host_image);
            if (isHostMatcher(host_image)) {
                PreferenceUtils.putString(context, PrefKey.HOST_IMAGE, host_image);
            } else {
                showToast("图片IP地址不合法");
                return;
            }
        } else {
            showToast("图片IP地址为空");
            return;
        }

        if (!TextUtils.isEmpty(port_image)) {
            PreferenceUtils.putString(context, PrefKey.PORT_IMAGE, port_image);
//            if (isPortMatcher(port_image)) {
//                PreferenceUtils.putInt(context, PrefKey.PORT_IMAGE, Integer.parseInt(port_image));
//            } else {
//                showToast("图片端口号不合法");
//                return;
//            }
        } else {
            showToast("图片端口为空");
            return;
        }
        PreferenceUtils.putBoolean(context, PrefKey.SETTED, true);
        TcpSocketClient.getInstance().resetSocket();
        finish();
    }

    private boolean isHostMatcher(String str) {
        Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }


    private boolean isPortMatcher(String str) {
        Pattern pattern = Pattern.compile("^([1-9]|[1-9]\\\\d{1,3}|[1-6][0-5][0-5][0-3][0-5])$");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}

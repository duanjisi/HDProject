
/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.client.setting;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.comm.PrefKey;
import com.atgc.hd.comm.socket.SocketTestManager;
import com.atgc.hd.comm.utils.PreferenceUtils;
import com.atgc.hd.widget.SuperEditText;
import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.SocketActionAdapter;

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

    private SocketTestManager socketTestManager;

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

        if (socketTestManager == null) {
            socketTestManager = new SocketTestManager();
        } else {
            socketTestManager.onDestory();
        }
        showProgressDialog("正在测试服务器地址是否可连接...");

        socketTestManager.initConfiguration(host, Integer.valueOf(port));
        socketTestManager.test(socketActionAdapter());
    }

    @Override
    protected void onDestroy() {
        if (socketTestManager != null) {
            socketTestManager.onDestory();
        }
        super.onDestroy();
    }

    private SocketActionAdapter socketActionAdapter() {
        return new SocketActionAdapter() {
            /**
             * 当Socket连接建立成功后<br>
             * 系统会回调该方法,此时有可能读写线程还未启动完成,不过不会影响大碍<br>
             * 当回调此方法后,我们可以认为Socket连接已经建立完成,并且读写线程也初始化完
             *
             * @param context
             * @param info 这次连接的连接信息
             * @param action
             */
            @Override
            public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
                super.onSocketConnectionSuccess(context, info, action);
                dismissProgressDialog();
                Log.e("socketManager", "onSocketConnectionSuccess 连接成功");
//                showMessage("连接成功！");
                setResult(RESULT_OK);
                finish();
            }

            /**
             * 当Socket连接失败时会进行回调<br>
             * 建立Socket连接,如果服务器出现故障,网络出现异常都将导致该方法被回调<br>
             * 系统回调此方法时,IO线程均未启动.如果IO线程启动将会回调{@link #onSocketDisconnection(Context, ConnectionInfo, String, Exception)}
             *
             * @param context
             * @param info 这次连接的连接信息
             * @param action
             * @param e 连接未成功建立的错误原因
             */
            @Override
            public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
                super.onSocketConnectionFailed(context, info, action, e);
                dismissProgressDialog();
                showMessage("服务器连接失败！");
                Log.e("socketManager", "onSocketConnectionFailed 连接失败");
            }
        };
    }

    private void showMessage(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(msg);
            }
        });
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

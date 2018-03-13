package com.atgc.hd.client.tasklist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.comm.utils.PreferenceUtils;
import com.atgc.hd.entity.EventMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * 模拟上传的坐标
 * <p>描述：
 * <p>作者：liangguokui 2018/3/12
 */
public class SimulationGPSActivity extends BaseActivity {
    public static final String KEY_LAT = "KEY_LAT_ATY";
    public static final String KEY_LNG = "KEY_LNG_ATY";

    @BindView(R.id.tv_content1)
    EditText edtLng;

    @BindView(R.id.tv_content2)
    EditText edtLat;

    @Override
    public void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simulation);
        ButterKnife.bind(this);

        String lat = PreferenceUtils.getString(context, KEY_LAT, "");
        String lng = PreferenceUtils.getString(context, KEY_LNG, "");

        edtLat.setText(lat);
        edtLng.setText(lng);
    }

    @Override
    public String toolBarTitle() {
        return "GPS坐标模拟";
    }

    @OnClick(R.id.btn_ok)
    public void clickSimulationGPS(View view) {

        String lat = edtLat.getText().toString();
        String lng = edtLng.getText().toString();

        PreferenceUtils.putString(context, KEY_LAT, lat);
        PreferenceUtils.putString(context, KEY_LNG, lng);

        EventBus.getDefault().post(new EventMessage("simulation_gps", lng + "#" + lat));

        hideKeyboard();
        showToast("修改完毕");
    }
}

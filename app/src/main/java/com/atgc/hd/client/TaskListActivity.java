package com.atgc.hd.client;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseActivity;

/**
 * <p>描述： 任务列表界面
 * <p>作者： liangguokui 2018/1/17
 */
public class TaskListActivity extends BaseActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_tasklist);
    }

    @Override
    public void onError(String msg) {

    }

    @Override
    public void onResponse(String json) {

    }
}

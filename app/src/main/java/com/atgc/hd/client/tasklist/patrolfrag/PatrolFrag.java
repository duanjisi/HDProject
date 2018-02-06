package com.atgc.hd.client.tasklist.patrolfrag;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseFragment;
import com.atgc.hd.client.tasklist.TaskHandContract;
import com.atgc.hd.client.tasklist.TaskListActivity;
import com.atgc.hd.client.tasklist.patrolfrag.adapter.PatrolAdapter;
import com.atgc.hd.client.tasklist.taskfrag.adapter.TaskListEntity;
import com.atgc.hd.comm.net.response.TaskListResponse;
import com.atgc.hd.comm.widget.NiftyDialog;
import com.orhanobut.logger.Logger;

import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

/**
 * <p>描述： 当天巡更任务列表
 * <p>作者： liangguokui 2018/1/18
 */
public class PatrolFrag extends BaseFragment implements PatrolContract.IView {
    private RecyclerView taskListRecyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private PatrolAdapter patrolAdapter;

    private PatrolContract.IPresenterView iPresenter;

    public static PatrolFrag createIntance() {
        return new PatrolFrag();
    }

    @Override
    public int layoutResourceId() {
        return R.layout.frag_tasklist;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        iPresenter = new PatrolPresenter(this);
    }

    private void initView() {
        taskListRecyclerView = findViewById(R.id.task_recyclerview);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        LinearLayoutManager manager = new LinearLayoutManager(parentActivity);
        taskListRecyclerView.setLayoutManager(manager);
        patrolAdapter = new PatrolAdapter(parentActivity, false);
        taskListRecyclerView.setAdapter(patrolAdapter);

        tvTips = findViewById(R.id.tv_error_tips);
    }

    private TextView tvTips;
    @Override
    public void showTips(final String tips) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTips.setText(tips);
            }
        });
    }

    @Override
    public void showFillReasonDialog(final String taskStatus, final String carryStatus) {
        NiftyDialog.create(parentActivity)
                .setCustomView(R.layout.layout_reason, parentActivity)
                .withCustomViewOnClick(R.id.btn_commit, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(parentActivity, "提交", Toast.LENGTH_LONG).show();

                    }
                })
                .show();
    }

    @Override
    public void refreshTaskList(final List<TaskListEntity> entities) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (entities == null || entities.isEmpty()) {
                    patrolAdapter.setNewData(null);
                    Toast.makeText(parentActivity, "该时间点暂无任务...", Toast.LENGTH_LONG).show();
                } else {
                    patrolAdapter.setNewData(entities);
                }
            }
        });
    }

    @Override
    public void registerOnCurrentTaskListener(TaskHandContract.OnCurrentTaskListener listener) {
        TaskListActivity aty = (TaskListActivity) parentActivity;
        aty.registerOnCurrentTaskListener(listener);
    }

    @Override
    public void onDestroy() {
        iPresenter.onDestory();
        super.onDestroy();
    }
}

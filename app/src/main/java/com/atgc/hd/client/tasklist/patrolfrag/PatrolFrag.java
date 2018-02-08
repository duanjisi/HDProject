package com.atgc.hd.client.tasklist.patrolfrag;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseFragment;
import com.atgc.hd.client.tasklist.patrolfrag.adapter.PatrolAdapter;
import com.atgc.hd.client.tasklist.taskfrag.adapter.TaskListEntity;
import com.atgc.hd.comm.utils.StringUtils;
import com.atgc.hd.comm.widget.NiftyDialog;

import java.util.List;

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

        View view = inflate(R.layout.layout_empty_data);
        patrolAdapter.setEmptyView(view);
    }

    @Override
    public void showTips(final String tips) {
    }

    @Override
    public void showFillReasonDialog(final String taskStatus, final String carryStatus) {
        View contentView = inflate(R.layout.layout_reason);
        final EditText edtReason = findViewById(contentView, R.id.edt_reason);

        NiftyDialog.create(parentActivity)
                .setCustomView(contentView)
                .withCustomViewOnClick(R.id.btn_commit, new NiftyDialog.OnClickActionListener() {
                    @Override
                    public void onClick(NiftyDialog dialog, View clickView) {
                        String reason = edtReason.getText().toString();
                        if (StringUtils.isEmpty(reason)) {
                            Toast.makeText(parentActivity, "请填写异常原因", Toast.LENGTH_SHORT).show();
                        } else {
                            reportTaskStatus(dialog, reason);
                        }
                    }

                    private void reportTaskStatus(final NiftyDialog dialog, String reason) {
                        iPresenter.reportTaskStatus(taskStatus, carryStatus, reason, new PatrolContract.OnReportTaskListener() {

                            @Override
                            public void onReportSuccess() {
                                dialog.dismiss();
                            }

                            @Override
                            public void onReportFail(String msg) {

                            }
                        });
                    }
                })
                .isCancelableOnTouchOutside(false)
                .show();

    }

    @Override
    public void refreshTaskList(final List<TaskListEntity> entities) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                patrolAdapter.setNewData(entities);
            }
        });
    }

    @Override
    public void onDestroy() {
        iPresenter.onDestory();
        super.onDestroy();
    }



}

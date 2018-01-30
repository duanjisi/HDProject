package com.atgc.hd.client.tasklist.taskfrag;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseFragment;
import com.atgc.hd.base.adapter.ViewHolder;
import com.atgc.hd.base.adapter.interfaces.OnItemClickListener;
import com.atgc.hd.client.tasklist.TaskHandContract;
import com.atgc.hd.client.tasklist.TaskListActivity;
import com.atgc.hd.client.tasklist.taskfrag.adapter.TaskListAdapter;
import com.atgc.hd.comm.net.response.TaskListResponse;

import java.util.List;

/**
 * <p>描述： 历史巡更任务列表
 * <p>作者： liangguokui 2018/1/18
 */
public class TaskListFrag extends BaseFragment implements TaskListContract.IView, OnItemClickListener<TaskListResponse.TaskInfo> {

    private RecyclerView historyTaskRecyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TaskListAdapter historyTaskListAdapter;

    private TaskListContract.IPresenterView iPresenter;

    public static TaskListFrag createIntance() {
        return new TaskListFrag();
    }

    @Override
    public int layoutResourceId() {
        return R.layout.frag_history_tasklist;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

        iPresenter = new TaskListPresenter(this);
    }

    private void initView() {
        historyTaskRecyclerView = findViewById(R.id.task_recyclerview);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        LinearLayoutManager manager = new LinearLayoutManager(parentActivity);
        historyTaskRecyclerView.setLayoutManager(manager);
        historyTaskListAdapter = new TaskListAdapter(parentActivity, false);
        historyTaskRecyclerView.setAdapter(historyTaskListAdapter);

        historyTaskListAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(ViewHolder viewHolder, TaskListResponse.TaskInfo data, int position) {
        if (iPresenter.isCurrentTask(data.getTaskID())) {
            TaskListActivity aty = (TaskListActivity) parentActivity;
            aty.showCurrentTaskPage();
        } else {

        }
    }

    @Override
    public void registerOnAllTaskListener(TaskHandContract.OnAllTaskLlistener listener) {
        TaskListActivity aty = (TaskListActivity) parentActivity;
        aty.registerOnAllTaskListener(listener);
    }

    @Override
    public void refreshTaskList(final List<TaskListResponse.TaskInfo> taskArray) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                historyTaskListAdapter.setNewData(taskArray);
            }
        });
    }

}


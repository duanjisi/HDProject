package com.atgc.hd.client.tasklist.tasklistfrag;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseFragment;
import com.atgc.hd.client.tasklist.tasklistfrag.adapter.TaskEntity;
import com.atgc.hd.client.tasklist.tasklistfrag.adapter.TaskListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>描述： 当天巡更任务列表
 * <p>作者： liangguokui 2018/1/18
 */
public class TaskListFrag extends BaseFragment {
    private RecyclerView taskListRecyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TaskListAdapter taskListAdapter;

    public static TaskListFrag createIntance() {
        return new TaskListFrag();
    }

    @Override
    public int layoutResourceId() {
        return R.layout.frag_tasklist;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }

    private void initView() {
        taskListRecyclerView = findViewById(R.id.task_recyclerview);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        LinearLayoutManager manager = new LinearLayoutManager(parentActivity);
        taskListRecyclerView.setLayoutManager(manager);
        taskListAdapter = new TaskListAdapter(parentActivity, false);
        taskListRecyclerView.setAdapter(taskListAdapter);

        List<TaskEntity> testData = new ArrayList<>();
        TaskEntity entity;
        for (int i = 0; i < 20; i++) {
            entity = new TaskEntity();
            entity.setPointArray("AS-湾流亭");
            entity.setStartTime("17:53");
            testData.add(entity);
        }
        taskListAdapter.setData(testData);
    }
}

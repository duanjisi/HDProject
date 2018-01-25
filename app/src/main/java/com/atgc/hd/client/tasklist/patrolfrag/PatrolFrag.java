package com.atgc.hd.client.tasklist.patrolfrag;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseFragment;
import com.atgc.hd.client.tasklist.TaskHandContract;
import com.atgc.hd.client.tasklist.patrolfrag.adapter.TaskListAdapter;
import com.atgc.hd.comm.net.response.TaskListResponse;

/**
 * <p>描述： 当天巡更任务列表
 * <p>作者： liangguokui 2018/1/18
 */
public class PatrolFrag extends BaseFragment implements PatrolContract.IView {
    private RecyclerView taskListRecyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TaskListAdapter taskListAdapter;

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
        Bundle bundle = getArguments();
        TaskHandContract taskHandContract = (TaskHandContract) bundle.getSerializable("taskHandContract");
        iPresenter.setTaskHandContract(taskHandContract);
    }

    private void initView() {
        taskListRecyclerView = findViewById(R.id.task_recyclerview);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        LinearLayoutManager manager = new LinearLayoutManager(parentActivity);
        taskListRecyclerView.setLayoutManager(manager);
        taskListAdapter = new TaskListAdapter(parentActivity, false);
        taskListRecyclerView.setAdapter(taskListAdapter);
    }

    @Override
    public void refreshTaskList(TaskListResponse.TaskInfo taskInfo) {
        taskListAdapter.setNewData(taskInfo.getPointArray());
    }

    @Override
    public void onDestroy() {
        iPresenter.onDestory();
        super.onDestroy();
    }

}

package com.atgc.hd.client.tasklist.taskfrag;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseFragment;
import com.atgc.hd.client.tasklist.TaskHandContract;
import com.atgc.hd.client.tasklist.taskfrag.adapter.HistoryTaskListAdapter;
import com.atgc.hd.comm.net.response.TaskListResponse;

import java.util.List;

/**
 * <p>描述： 历史巡更任务列表
 * <p>作者： liangguokui 2018/1/18
 */
public class TaskListFrag extends BaseFragment implements TaskListContract.IView {

    private RecyclerView historyTaskRecyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private HistoryTaskListAdapter historyTaskListAdapter;

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
        Bundle bundle = getArguments();
        TaskHandContract taskHandContract = (TaskHandContract) bundle.getSerializable("taskHandContract");
        iPresenter.setTaskHandContract(taskHandContract);
    }

    private void initView() {
        historyTaskRecyclerView = findViewById(R.id.task_recyclerview);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setEnabled(false);

        LinearLayoutManager manager = new LinearLayoutManager(parentActivity);
        historyTaskRecyclerView.setLayoutManager(manager);
        historyTaskListAdapter = new HistoryTaskListAdapter(parentActivity, false);
        historyTaskRecyclerView.setAdapter(historyTaskListAdapter);
    }

    @Override
    public void refreshTaskList(List<TaskListResponse.TaskInfo> taskArray) {
        historyTaskListAdapter.setNewData(taskArray);
    }
}


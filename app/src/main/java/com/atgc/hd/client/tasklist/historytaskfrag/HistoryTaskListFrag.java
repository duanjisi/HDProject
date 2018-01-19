package com.atgc.hd.client.tasklist.historytaskfrag;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseFragment;
import com.atgc.hd.client.tasklist.historytaskfrag.adapter.HistoryTaskListAdapter;

/**
 * <p>描述： 历史巡更任务列表
 * <p>作者： liangguokui 2018/1/18
 */
public class HistoryTaskListFrag extends BaseFragment {

    private RecyclerView historyTaskRecyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private HistoryTaskListAdapter historyTaskListAdapter;

    public static HistoryTaskListFrag createIntance() {
        return new HistoryTaskListFrag();
    }

    @Override
    public int layoutResourceId() {
        return R.layout.frag_history_tasklist;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
    }

    private void initView() {
        historyTaskRecyclerView = findViewById(R.id.task_recyclerview);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        historyTaskListAdapter = new HistoryTaskListAdapter(parentActivity, false);
        historyTaskRecyclerView.setAdapter(historyTaskListAdapter);
    }
}


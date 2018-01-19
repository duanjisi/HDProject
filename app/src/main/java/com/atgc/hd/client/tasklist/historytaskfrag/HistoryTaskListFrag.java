package com.atgc.hd.client.tasklist.historytaskfrag;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseFragment;
import com.atgc.hd.base.adapter.ViewHolder;
import com.atgc.hd.base.adapter.interfaces.OnItemClickListener;
import com.atgc.hd.client.tasklist.historytaskfrag.adapter.HistoryTaskEntity;
import com.atgc.hd.client.tasklist.historytaskfrag.adapter.HistoryTaskListAdapter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

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
        swipeRefreshLayout.setEnabled(false);

        LinearLayoutManager manager = new LinearLayoutManager(parentActivity);
        historyTaskRecyclerView.setLayoutManager(manager);
        historyTaskListAdapter = new HistoryTaskListAdapter(parentActivity, false);
        historyTaskRecyclerView.setAdapter(historyTaskListAdapter);

        List<HistoryTaskEntity> historyTaskEntities = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            HistoryTaskEntity entity = new HistoryTaskEntity();
            entity.setHistoryTaskTitle("2018年1月19日");
            entity.setTaskPeriod("08:00-10:15");
            entity.setTaskStatus("全部打点");

            historyTaskEntities.add(entity);
        }

        historyTaskListAdapter.setData(historyTaskEntities);

        historyTaskListAdapter.setOnItemClickListener(new OnItemClickListener<HistoryTaskEntity>() {
            @Override
            public void onItemClick(ViewHolder viewHolder, HistoryTaskEntity data, int position) {
                Logger.e("位置：" + position);
            }
        });
    }
}


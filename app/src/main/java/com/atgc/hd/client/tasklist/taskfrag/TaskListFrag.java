package com.atgc.hd.client.tasklist.taskfrag;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.atgc.hd.R;
import com.atgc.hd.base.BaseFragment;
import com.atgc.hd.base.adapter.animator.FadeInDownAnimator;
import com.atgc.hd.base.adapter.holder.ViewHolder;
import com.atgc.hd.base.adapter.interfaces.OnMultiItemClickListeners;
import com.atgc.hd.client.tasklist.taskfrag.adapter.TaskListAdapter;
import com.atgc.hd.client.tasklist.taskfrag.adapter.TaskListEntity;
import com.atgc.hd.entity.EventMessage;
import com.orhanobut.logger.Logger;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * <p>描述： 历史巡更任务列表
 * <p>作者： liangguokui 2018/1/18
 */
public class TaskListFrag extends BaseFragment implements TaskListContract.IView, OnMultiItemClickListeners<TaskListEntity> {

    private RecyclerView historyTaskRecyclerView;

    private SwipeRefreshLayout swipeRefreshLayout;

    private TaskListAdapter taskListAdapter;

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
        taskListAdapter = new TaskListAdapter(parentActivity, false);
        historyTaskRecyclerView.setAdapter(taskListAdapter);

        SimpleItemAnimator itemAnimator = new FadeInDownAnimator();
        itemAnimator.setSupportsChangeAnimations(false);
        historyTaskRecyclerView.setItemAnimator(itemAnimator);
        historyTaskRecyclerView.getItemAnimator().setAddDuration(400);
        historyTaskRecyclerView.getItemAnimator().setRemoveDuration(300);
        taskListAdapter.setOnMultiItemClickListener(this);

        View view = inflate(R.layout.layout_empty_data);
        taskListAdapter.setEmptyView(view);
    }

    @Override
    public void onItemClick(ViewHolder viewHolder, TaskListEntity data, int position, int viewType) {
        // 点击的是group item
        if (TaskListEntity.ITEM_GROUP == viewType) {
            if (data.getTaskInfo() == null) {
                Logger.w("出错了，点击的是itemgroup，但taskInfo为null");
                return;
            }
            // 当点击的任务是正在进行的任务时，则切换到当前任务页面
            if (iPresenter.isCurrentTask(data.getTaskInfo().getTaskID())) {
                EventBus.getDefault().post(new EventMessage("switch_task_page", 0));
                return;
            }

            // 已是展开状态，则关闭
            if (data.isGroupExpand()) {
                data.setGroupExpand(false);
                viewHolder.getView(R.id.iv_background).setSelected(false);
                viewHolder.getView(R.id.iv_arrow).setSelected(false);
                int childCount = data.getTaskInfo().getPointArray().size();
                taskListAdapter.removeWithAnimation(position + 1, childCount);
            }
            // 已是关闭状态，则展开
            else {
                data.setGroupExpand(true);
                viewHolder.getView(R.id.iv_background).setSelected(true);
                viewHolder.getView(R.id.iv_arrow).setSelected(true);
                taskListAdapter.addWithAnimation(position + 1, data.getPointInfoEntities());
            }
        }
        // 点击的是child item
        else {
        }
    }

    @Override
    public void refreshTaskList(final List<TaskListEntity> entities) {
        parentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                taskListAdapter.setNewData(entities);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}


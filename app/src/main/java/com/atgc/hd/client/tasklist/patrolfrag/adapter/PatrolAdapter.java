package com.atgc.hd.client.tasklist.patrolfrag.adapter;

import android.content.Context;

import com.atgc.hd.R;
import com.atgc.hd.base.adapter.holder.ViewHolder;
import com.atgc.hd.client.tasklist.taskfrag.adapter.TaskListAdapter;
import com.atgc.hd.client.tasklist.taskfrag.adapter.TaskListEntity;

/**
 * <p>描述： 当天巡更任务列表适配器
 * <p>作者： liangguokui 2018/1/18
 */
public class PatrolAdapter extends TaskListAdapter {

    public PatrolAdapter(Context context, boolean isOpenLoadMore) {
        super(context, isOpenLoadMore);
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == TaskListEntity.ITEM_GROUP) {
            return R.layout.item_group_current_task;
        } else {
            return super.getItemLayoutId(viewType);
        }
    }

    @Override
    protected void convertGroupItem(ViewHolder holder, TaskListEntity data, int position) {
        holder.getConvertView().setSelected(true);

        holder.setText(R.id.tv_task_title, data.getTaskInfo().getTaskName());
        holder.setText(R.id.tv_task_peroid, data.getTaskInfo().getTaskPeriod());

        holder.getView(R.id.iv_background).setSelected(true);
    }

}

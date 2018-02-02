package com.atgc.hd.client.tasklist.patrolfrag.adapter;

import android.content.Context;
import android.view.View;

import com.atgc.hd.R;
import com.atgc.hd.base.adapter.ViewHolder;
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
//        super.convertGroupItem(holder, data, position);

        holder.setText(R.id.tv_task_title, data.getTaskInfo().getTaskName());
        holder.setText(R.id.tv_task_peroid, data.getTaskInfo().getTaskPeriod());

        holder.getView(R.id.iv_background).setSelected(true);
    }

    //    @Override
//    protected int getItemLayoutId() {
//        return R.layout.item_tasklist;
//    }
//
//    @Override
//    protected void convert(ViewHolder holder, PointInfo data, int position) {
//        int temp = position + 1;
//        String strPositioin = temp < 10 ? ("0" + temp + ".") : (temp + ".");
//        holder.setText(R.id.tv_point_position, strPositioin);
//
//        holder.setText(R.id.tv_point_status, data.getPointName());
//
////        holder.setText(R.id.tv_task_start_time, data.getStartTime());
//
//        holder.setText(R.id.tv_task_finish_time, "--:--");
//
//        String status = data.isChecked() ? "已打点" : "未打点";
//        holder.setText(R.id.tv_task_title, status);
//    }
}

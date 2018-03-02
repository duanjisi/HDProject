package com.atgc.hd.client.tasklist.taskfrag.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.base.adapter.MultiBaseAdapter;
import com.atgc.hd.base.adapter.holder.ViewHolder;
import com.atgc.hd.comm.net.response.TaskListResponse.TaskInfo;

/**
 * <p>描述： 巡更任务列表适配器
 * <p>作者： liangguokui 2018/1/18
 */
public class TaskListAdapter extends MultiBaseAdapter<TaskListEntity> {
    private int colorGray;
    private int colorBlue;
    private int colorGreen;
    private int colorPrimary;

    public TaskListAdapter(Context context, boolean isOpenLoadMore) {
        super(context, isOpenLoadMore);

        colorGray = context.getResources().getColor(R.color.meta_text_tertiary);
        colorBlue = context.getResources().getColor(R.color.dark_blue);
        colorGreen = context.getResources().getColor(R.color.color_green);
        colorPrimary = context.getResources().getColor(R.color.meta_text_primary);
    }

    @Override
    protected int getViewType(int position, TaskListEntity data) {
        return data.getEntityType();
    }

    @Override
    protected int getItemLayoutId(int viewType) {
        if (viewType == TaskListEntity.ITEM_GROUP) {
            return R.layout.item_group_tasklist;
        } else {
            return R.layout.item_child_tasklist;
        }
    }

    @Override
    protected void convert(ViewHolder holder, TaskListEntity data, int position, int viewType) {
        if (viewType == TaskListEntity.ITEM_GROUP) {
            convertGroupItem(holder, data, position);
        } else {
            convertChildItem(holder, data, position);
        }
    }

    protected void convertGroupItem(ViewHolder holder, TaskListEntity data, int position) {
        TaskInfo taskInfo = data.getTaskInfo();

        holder.setText(R.id.tv_position, data.getGroupPosition());

        holder.getConvertView().setSelected(data.isGroupExpand());

        TextView tvPointStatus = holder.getView(R.id.tv_task_status);
        TextView tvTaskTitle = holder.getView(R.id.tv_task_title);
        TextView tvTaskPeriod = holder.getView(R.id.tv_task_peroid);
        ImageView ivTaskStatus = holder.getView(R.id.iv_task_status);

        tvTaskTitle.setText(taskInfo.getTaskName());
        tvTaskPeriod.setText(taskInfo.getTaskPeriod());
        tvPointStatus.setTextColor(colorPrimary);

        tvTaskTitle.setSelected(true);
        tvTaskPeriod.setSelected(true);

        String taskStatus = taskInfo.getTaskStatus();
        if ("1".equals(taskStatus)) {
            tvPointStatus.setText("暂未开始");
            tvTaskTitle.setSelected(false);
            tvTaskPeriod.setSelected(false);
        } else if ("2".equals(taskStatus)) {
            tvPointStatus.setText("进行中...");
            tvPointStatus.setTextColor(colorGreen);
        } else if ("1".equals(taskInfo.getInspectStatus())
                && ("3".equals(taskStatus) || "4".equals(taskStatus))) {
            tvPointStatus.setText("已全部打点");
        } else {
            tvPointStatus.setText("存在异常");
        }

        // 巡查任务未开始
        if ("1".equals(taskStatus)) {
            ivTaskStatus.setImageResource(R.drawable.task_status_gray);
        }
        // 巡查任务进行中
        else if ("2".equals(taskStatus)) {
            ivTaskStatus.setImageResource(R.drawable.task_status_green);
        }
        // 巡查任务已过计划时间点且所有点已正常巡查
        else if ("1".equals(taskInfo.getInspectStatus())
                && ("3".equals(taskStatus) || "4".equals(taskStatus))) {
            ivTaskStatus.setImageResource(R.drawable.task_status_green);
        }
        // 所有点已巡查但有异常点或其他异常情况
        else {
            ivTaskStatus.setImageResource(R.drawable.task_status_red);
        }

    }

    protected void convertChildItem(ViewHolder holder, TaskListEntity data, int position) {
        int firstVisibility = data.isFirstPointInfo() ? View.GONE : View.VISIBLE;
        holder.getView(R.id.iv_line1).setVisibility(firstVisibility);

        int lastVisibility = data.isLastPointInfo() ? View.GONE : View.VISIBLE;
        holder.getView(R.id.iv_line3).setVisibility(lastVisibility);

        ImageView ivPosintTag = holder.getView(R.id.iv_tag_location);
        if (data.isFirstPointInfo()) {
            ivPosintTag.setVisibility(View.VISIBLE);
            ivPosintTag.setImageResource(R.drawable.location_start);
        } else if (data.isLastPointInfo()) {
            ivPosintTag.setVisibility(View.VISIBLE);
            if (data.getPointInfo().isChecked()) {
                ivPosintTag.setImageResource(R.drawable.location_end_blue);
            } else {
                ivPosintTag.setImageResource(R.drawable.location_end_gray);
            }
        } else {
            ivPosintTag.setVisibility(View.GONE);
        }

        ImageView ivPointCheck = holder.getView(R.id.iv_dot0);
        TextView tvPointStatus = holder.getView(R.id.tv_task_status);
        TextView tvCheckedTime = holder.getView(R.id.tv_position);
        // 未巡查
        if ("0".equals(data.getPointInfo().getResultType())) {
            ivPointCheck.setImageResource(R.drawable.ic_gray_uncheck);
            tvPointStatus.setText("未打点");
            tvPointStatus.setBackgroundResource(R.color.transparent_gray);
            tvCheckedTime.setTextColor(colorGray);
        }
        // 正常已巡查
        else if ("1".equals(data.getPointInfo().getResultType())) {
            ivPointCheck.setImageResource(R.drawable.ic_green_checked);
            tvPointStatus.setText("已打点");
            tvPointStatus.setBackgroundResource(R.color.transparent_green);
            tvCheckedTime.setTextColor(colorBlue);
        }
        // 超时未巡查
        else if ("2".equals(data.getPointInfo().getResultType())) {
            ivPointCheck.setImageResource(R.drawable.ic_red_exception);
            tvPointStatus.setText("超时未巡查");
            tvPointStatus.setBackgroundResource(R.color.transparent_red);
            tvCheckedTime.setTextColor(colorBlue);
        }
        // 超时已巡查
        else if ("3".equals(data.getPointInfo().getResultType())) {
            ivPointCheck.setImageResource(R.drawable.ic_red_exception);
            tvPointStatus.setText("超时已巡查");
            tvPointStatus.setBackgroundResource(R.color.transparent_red);
            tvCheckedTime.setTextColor(colorBlue);
        }
        // 其他异常情况
        else {
            ivPointCheck.setImageResource(R.drawable.ic_gray_uncheck);
            tvPointStatus.setText("点状态异常");
            tvPointStatus.setBackgroundResource(R.color.transparent_gray);
            tvCheckedTime.setTextColor(colorGray);

        }
        tvCheckedTime.setText(data.getPointInfo().getPointTime());

        holder.setText(R.id.tv_location_name, data.getPointInfo().getPointName());
        holder.setText(R.id.tv_timeline, data.getPointInfo().getPlanTime());
        holder.setText(R.id.tv_position, data.getPointInfo().getFormatPointTime());
        holder.setText(R.id.tv_timeline, data.getPointInfo().getFormatPlanTime());

    }

}

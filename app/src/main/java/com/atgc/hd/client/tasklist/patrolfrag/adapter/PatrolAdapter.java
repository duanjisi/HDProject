package com.atgc.hd.client.tasklist.patrolfrag.adapter;

import android.content.Context;

import com.atgc.hd.R;
import com.atgc.hd.base.adapter.BaseSingleAdapter;
import com.atgc.hd.base.adapter.ViewHolder;
import com.atgc.hd.comm.net.response.TaskListResponse.PointInfo;

/**
 * <p>描述： 当天巡更任务列表适配器
 * <p>作者： liangguokui 2018/1/18
 */
public class PatrolAdapter extends BaseSingleAdapter<PointInfo> {

    public PatrolAdapter(Context context, boolean isOpenLoadMore) {
        super(context, isOpenLoadMore);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_tasklist;
    }

    @Override
    protected void convert(ViewHolder holder, PointInfo data, int position) {
        int temp = position + 1;
        String strPositioin = temp < 10 ? ("0" + temp + ".") : (temp + ".");
        holder.setText(R.id.tv_position, strPositioin);

        holder.setText(R.id.tv_task_title, data.getPointName());

//        holder.setText(R.id.tv_task_start_time, data.getStartTime());

        holder.setText(R.id.tv_task_finish_time, "--:--");

        String status = data.isChecked() ? "已打点" : "未打点";
        holder.setText(R.id.tv_task_tag, status);
    }

    public void checkPoint() {

    }
}

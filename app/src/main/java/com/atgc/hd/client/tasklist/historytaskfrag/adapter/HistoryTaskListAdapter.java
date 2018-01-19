package com.atgc.hd.client.tasklist.historytaskfrag.adapter;

import android.content.Context;

import com.atgc.hd.R;
import com.atgc.hd.base.adapter.BaseSingleAdapter;
import com.atgc.hd.base.adapter.ViewHolder;

/**
 * <p>描述： 历史巡更任务适配器
 * <p>作者： liangguokui 2018/1/18
 */
public class HistoryTaskListAdapter extends BaseSingleAdapter<HistoryTaskEntity> {

    public HistoryTaskListAdapter(Context context, boolean isOpenLoadMore) {
        super(context, isOpenLoadMore);
    }

    @Override
    protected void convert(ViewHolder holder, HistoryTaskEntity data, int position) {
        int temp = position + 1;
        String strPositioin = temp < 10 ? ("0" + temp + ".") : (temp + ".");
        holder.setText(R.id.tv_position, strPositioin);

        holder.setText(R.id.tv_task_title, data.getHistoryTaskTitle());

        holder.setText(R.id.tv_task_period, data.getTaskPeriod());

        holder.setText(R.id.tv_task_tag, data.getTaskStatus());
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_history_tasklist;
    }
}

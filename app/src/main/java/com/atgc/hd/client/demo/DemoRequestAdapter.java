package com.atgc.hd.client.demo;

import android.content.Context;

import com.atgc.hd.R;
import com.atgc.hd.base.adapter.BaseSingleAdapter;
import com.atgc.hd.base.adapter.ViewHolder;

/**
 * <p>描述：
 * <p>作者：liangguokui 2018/1/31
 */
public class DemoRequestAdapter extends BaseSingleAdapter<String> {
    public DemoRequestAdapter(Context context, boolean isOpenLoadMore) {
        super(context, isOpenLoadMore);
    }

    @Override
    protected int getItemLayoutId() {
        return R.layout.item_demo_request;
    }

    @Override
    protected void convert(ViewHolder holder, String data, int position) {
        holder.setText(R.id.tv_point_status, data);
    }
}

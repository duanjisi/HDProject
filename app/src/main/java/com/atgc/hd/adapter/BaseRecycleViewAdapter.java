/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.adapter;

import android.support.v7.widget.RecyclerView;


import com.atgc.hd.listener.RecycleViewItemListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by GMARUnity on 2017/2/3.
 */
public abstract class BaseRecycleViewAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    protected RecycleViewItemListener itemListener;
    protected List<T> datas = new ArrayList<T>();

    public List<T> getDatas() {
        if (datas == null)
            datas = new ArrayList<T>();
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public void setItemListener(RecycleViewItemListener listener) {
        this.itemListener = listener;
    }


    @Override
    public void onBindViewHolder(VH holder, int position) {
        onBindItemHolder(holder, position);
    }

    //局部刷新关键：带payload的这个onBindViewHolder方法必须实现
    @Override
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindItemHolder(holder, position);
        } else {
            onBindItemHolder(holder, position, payloads);
        }

    }

    public abstract void onBindItemHolder(VH holder, int position);

    public void onBindItemHolder(VH holder, int position, List<Object> payloads) {

    }

    public void remove(int position) {
        this.datas.remove(position);
        notifyItemRemoved(position);

        if (position != (datas.size())) { // 如果移除的是最后一个，忽略
            notifyItemRangeChanged(position, this.datas.size() - position);
        }
    }

    public void setDataList(Collection<T> list) {
        this.datas.clear();
        this.datas.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(Collection<T> list) {
        int lastIndex = this.datas.size();
        if (this.datas.addAll(list)) {
            notifyItemRangeInserted(lastIndex, list.size());
        }
    }
}

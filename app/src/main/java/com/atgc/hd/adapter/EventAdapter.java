/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.base.adapter.BaseAdapter;
import com.atgc.hd.entity.EventEntity;

/**
 * <p>描述：事件列表
 * <p>作者：duanjisi 2018年 02月 01日
 */

public class EventAdapter extends ABaseAdapter<EventEntity> {

    private onItemClickListener itemClickListener = null;

    public EventAdapter(Context context, onItemClickListener listener) {
        super(context);
        this.itemClickListener = listener;
    }

//    private EventAdapter(Context context) {
//        super(context);
//    }

    @Override
    protected View setConvertView(int position, final EventEntity entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_event, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (entity != null) {
            setTextView(holder.tv_des, entity.getDescription());
            setTextView(holder.tv_time, entity.getTime());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(entity);
                    }
                }
            });
        }
        return convertView;
    }

    public interface onItemClickListener {
        void onItemClick(EventEntity event);
    }

    private static class ViewHolder {
        TextView tv_des;
        TextView tv_time;

        public ViewHolder(View view) {
            this.tv_des = view.findViewById(R.id.tv_descrip);
            this.tv_time = view.findViewById(R.id.tv_time);
        }
    }
}

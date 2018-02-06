/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.entity.PatInfo;

/**
 * <p>描述：平台系统消息
 * <p>作者：duanjisi 2018年 02月 05日
 */

public class PlatformInfoAdapter extends ABaseAdapter<PatInfo> {

    public PlatformInfoAdapter(Context context) {
        super(context);
    }

    @Override
    protected View setConvertView(int position, PatInfo entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_platform_info, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (entity != null) {
            setTextView(holder.tv_content, entity.getMessageContent());
            setTextView(holder.tv_time, entity.getSendTime());
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView tv_time;
        TextView tv_title;
        TextView tv_content;

        public ViewHolder(View view) {
            this.tv_time = view.findViewById(R.id.tv_time);
            this.tv_title = view.findViewById(R.id.tv_title);
            this.tv_content = view.findViewById(R.id.tv_content);
        }
    }
}

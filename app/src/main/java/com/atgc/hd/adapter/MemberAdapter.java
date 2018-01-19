/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.comm.utils.ImageLoaderUtils;
import com.atgc.hd.entity.Member;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * <p>描述：成员列表适配器
 * <p>作者：duanjisi 2018年 01月 18日
 */

public class MemberAdapter extends ABaseAdapter<Member> {

    private ImageLoader imageLoader;

    public MemberAdapter(Context context) {
        super(context);
        imageLoader = ImageLoaderUtils.createImageLoader(context);
    }

    @Override
    protected View setConvertView(int position, Member entity, View convertView) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.item_member_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (entity != null) {
            setTextView(holder.tv_device, entity.getDevice());
            setTextView(holder.tv_identy, entity.getIdentity());
            setTextView(holder.tv_name, entity.getUserName());
            setTextView(holder.tv_time, entity.getTime());
            String url = entity.getImage();
            if (!TextUtils.isEmpty(url)) {
                imageLoader.displayImage(url, holder.image, ImageLoaderUtils.getDisplayImageOptions());
            }
        }
        return convertView;
    }

    private static class ViewHolder {
        ImageView image;
        TextView tv_device;
        TextView tv_identy;
        TextView tv_name;
        TextView tv_time;

        public ViewHolder(View view) {
            this.image = view.findViewById(R.id.iv_head);
            this.tv_device = view.findViewById(R.id.tv_device);
            this.tv_identy = view.findViewById(R.id.tv_identy);
            this.tv_name = view.findViewById(R.id.tv_name);
            this.tv_time = view.findViewById(R.id.tv_time);
        }
    }
}

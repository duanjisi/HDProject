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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.atgc.hd.R;
import com.atgc.hd.comm.net.http.MyAsyncTask;
import com.atgc.hd.comm.utils.FileUtil;
import com.atgc.hd.comm.utils.UIUtils;
import com.atgc.hd.entity.UploadEntity;
import com.atgc.hd.widget.RoundImageView;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>描述：
 * <p>作者：duanjisi 2018年 01月 22日
 */

public class PicAdapter extends BaseAdapter {
    private final static String TAG = PicAdapter.class.getSimpleName();
    private ImageLoader imageLoader;
    private Context context;
    private float mImageHeight;
    private ArrayList<String> images;

    public PicAdapter(Context context) {
        this.context = context;
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 80)) / 3;
        if (images == null) {
            images = new ArrayList<>();
        }
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int i) {
        return images.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_picture2, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        convertView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
        convertView.getLayoutParams().width = (int) mImageHeight;
        convertView.getLayoutParams().height = (int) mImageHeight;
        final String url = images.get(position);
        String fileName = FileUtil.getFileName(url);
        if (fileName.contains(".mp4") || fileName.contains(".MP4")) {
            holder.ivPlay.setVisibility(View.VISIBLE);
        } else {
            holder.ivPlay.setVisibility(View.GONE);
        }
        Glide.with(context).load(url).
                placeholder(R.drawable.zf_default_message_image).
                crossFade().into(holder.ivPic);
        holder.tvProgress.setVisibility(View.GONE);
        holder.ivBg.setVisibility(View.GONE);
        holder.ivClose.setVisibility(View.GONE);
        return convertView;
    }



    public void addDatas(ArrayList<String> items) {
        if (images != null) {
            images.addAll(items);
        }
        notifyDataSetChanged();
    }

//    private ArrayList<UploadEntity> getBefore(ArrayList<UploadEntity> items, int count) {
//        ArrayList<UploadEntity> list = new ArrayList<>();
//        if (count < items.size() || count == items.size()) {
//            for (int i = 0; i < count; i++) {
//                list.add(items.get(i));
//            }
//        } else {
//            for (int i = 0; i < items.size(); i++) {
//                list.add(items.get(i));
//            }
//        }
//        return list;
//    }


    public static class ViewHolder {
        public ImageView ivClose;
        public ImageView ivPlay;
        public RoundImageView ivPic;
        public RoundImageView ivBg;
        public TextView tvProgress;

        public ViewHolder(View view) {
            this.ivClose = view.findViewById(R.id.iv_close);
            this.ivPlay = view.findViewById(R.id.iv_play);
            this.ivPic = view.findViewById(R.id.iv_image);
            this.ivBg = view.findViewById(R.id.iv_bg);
            this.tvProgress = view.findViewById(R.id.tv_progress);
        }
    }
}

/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
import com.atgc.hd.comm.net.http.MyTask;
import com.atgc.hd.comm.utils.PreferenceUtils;
import com.atgc.hd.comm.utils.UIUtils;
import com.atgc.hd.entity.UploadEntity;
import com.atgc.hd.widget.MyView;
import com.atgc.hd.widget.RoundImageView;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.WeakHashMap;

/**
 * <p>描述：
 * <p>作者：duanjisi 2018年 01月 22日
 */

public class PictureAdapter extends BaseAdapter {
    private final static String TAG = PictureAdapter.class.getSimpleName();
    private ImageLoader imageLoader;
    private Context context;
    private float mImageHeight;
    private ArrayList<UploadEntity> images;
    private boolean isAddPager = false;
    private WeakHashMap<String, View> maps = new WeakHashMap<>();
    private UpdateCallback updateCallback;

    public void setUpdateCallback(UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }

    public PictureAdapter(Context context) {
        this.context = context;
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 4;
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
//            convertView = View.inflate(context, R.layout.item_picture, null);
            convertView = LayoutInflater.from(context).inflate(R.layout.item_picture, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
        convertView.getLayoutParams().width = (int) mImageHeight;
        convertView.getLayoutParams().height = (int) mImageHeight;
        final UploadEntity entity = images.get(position);
        final String localPath = entity.getLocalPath();
        holder.myView.setCloseListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        holder.myView.startUpload(localPath);
        return convertView;
    }

    public void initData() {
        images.clear();
        UploadEntity entity = new UploadEntity();
        entity.setLocalPath("lastItem");
        images.add(entity);
        notifyDataSetChanged();
    }

    public void addItem2(UploadEntity entity) {
        int totals = images.size() - 1;
        if (!images.contains(entity)) {
            addItem(entity, totals);
            notifyDataSetChanged();
        } else {
            Toast.makeText(context, "已添加", Toast.LENGTH_LONG).show();
        }
    }

    public void addItem(UploadEntity entity, int index) {
        if (entity != null) {
            images.add(index, entity);
            notifyDataSetChanged();
        }
    }

    private void removeItem(UploadEntity love) {
        Iterator<UploadEntity> stringIterator = images.iterator();
        while (stringIterator.hasNext()) {
            UploadEntity s = stringIterator.next();
            if (s.getLocalPath().equals(love.getLocalPath())) {
                stringIterator.remove();
            }
        }
        UploadEntity emo = new UploadEntity();
        emo.setLocalPath("lastItem");
        if (!isContain(emo)) {
            images.add(emo);
        }
        notifyDataSetChanged();
    }

    private boolean isContain(UploadEntity love) {
        boolean flag = false;
        for (UploadEntity e : images) {
            if (e.getLocalPath().equals(love.getLocalPath())) {
                flag = true;
            }
        }
        return flag;
    }

    public static class ViewHolder {
        public MyView myView;

        public ViewHolder(View view) {
            this.myView = (MyView) view.findViewById(R.id.my_view);
        }
    }
}

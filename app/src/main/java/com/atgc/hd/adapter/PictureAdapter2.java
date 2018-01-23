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

public class PictureAdapter2 extends BaseAdapter {
    private final static String TAG = PictureAdapter2.class.getSimpleName();
    private ImageLoader imageLoader;
    private Context context;
    private float mImageHeight;
    private ArrayList<UploadEntity> images;
    private boolean isAddPager = false;
    private WeakHashMap<String, View> maps = new WeakHashMap<>();
    private UpdateCallback updateCallback;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                showProgress((String) msg.obj);
            }
        }
    };
    private MyTask task = null;

    private void showProgress(final String path) {
        View convertView = maps.get(path);
        if (convertView != null) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            final TextView tvProgress = holder.tvProgress;
            final ImageView ivClose = holder.ivClose;
            final RoundImageView ivBg = holder.ivBg;
            task = new MyTask(context, path, new MyTask.callBack() {
                @Override
                public void onPreExecute() {
                    tvProgress.setVisibility(View.VISIBLE);
                    ivBg.setVisibility(View.VISIBLE);
                    ivClose.setVisibility(View.GONE);
                    tvProgress.setText("开始上传");
                }

                @Override
                public void onPostExecute(String string) {
                    tvProgress.setVisibility(View.GONE);
                    ivBg.setVisibility(View.GONE);
                    ivClose.setVisibility(View.VISIBLE);
//                    entity.setUrl(string);
                    PreferenceUtils.putBoolean(context, path, true);
                }

                @Override
                public void onProgressUpdate(int progress) {
                    tvProgress.setText("" + progress + "%");
                }
            });
            task.execute();
        }
    }

    public void setUpdateCallback(UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }

    public PictureAdapter2(Context context) {
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
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final UploadEntity entity = images.get(position);
        String key = entity.getLocalPath();
        View convertView = maps.get(key);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_picture, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            if (!maps.containsKey(key)) {
                maps.put(key, convertView);
            }
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
        convertView.getLayoutParams().width = (int) mImageHeight;
        convertView.getLayoutParams().height = (int) mImageHeight;
        final String localPath = entity.getLocalPath();
        if (!localPath.equals("lastItem")) {
            Glide.with(context).load(localPath).
                    placeholder(R.drawable.zf_default_message_image).
                    crossFade().into(holder.ivPic);
            boolean upLoaded = PreferenceUtils.getBoolean(context, localPath, false);
            if (!upLoaded) {
                MyTask task = entity.getTask(context);
                final View finalConvertView = convertView;
//                holder.ivPic.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
////                        if (updateCallback != null) {
////                            updateCallback.freshProgress(finalConvertView, entity, position);
////                        }
//                        Message message = handler.obtainMessage();
//                        message.what = 1;
//                        message.obj = localPath;
//                        handler.sendMessage(message);
//                    }
//                });
//                MyTask myTask = new MyTask(context, localPath, new MyTask.callBack() {
//                    @Override
//                    public void onPreExecute() {
//                        finalHolder.tvProgress.setVisibility(View.VISIBLE);
//                        finalHolder.ivBg.setVisibility(View.VISIBLE);
//                        finalHolder.ivClose.setVisibility(View.GONE);
//                        finalHolder.tvProgress.setText("正在上传");
//                    }
//
//                    @Override
//                    public void onPostExecute(String string) {
//                        finalHolder.tvProgress.setVisibility(View.GONE);
//                        entity.setUrl(string);
//                        PreferenceUtils.putBoolean(context, localPath, true);
//                    }
//
//                    @Override
//                    public void onProgressUpdate(int progress) {
//                        finalHolder.tvProgress.setText(progress + "%" + "\n" + "正在上传");
//                    }
//                });
//                myTask.execute();
            } else {
                holder.tvProgress.setVisibility(View.GONE);
                holder.ivBg.setVisibility(View.GONE);
                holder.ivClose.setVisibility(View.VISIBLE);
                holder.ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeItem(entity);
                    }
                });
            }
        } else {
            holder.tvProgress.setVisibility(View.GONE);
            holder.ivBg.setVisibility(View.GONE);
            holder.ivClose.setVisibility(View.GONE);
            holder.ivBg.setImageResource(R.drawable.compose_pic_add);
        }
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
        public ImageView ivClose;
        public RoundImageView ivPic;
        public RoundImageView ivBg;
        public TextView tvProgress;

        public ViewHolder(View view) {
            this.ivClose = (ImageView) view.findViewById(R.id.iv_close);
            this.ivPic = (RoundImageView) view.findViewById(R.id.iv_image);
            this.ivBg = (RoundImageView) view.findViewById(R.id.iv_bg);
            this.tvProgress = view.findViewById(R.id.tv_progress);
        }
    }
}

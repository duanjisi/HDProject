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
import com.atgc.hd.comm.net.http.MyAsyncTask;
import com.atgc.hd.comm.net.http.MyTask;
import com.atgc.hd.comm.utils.FileUtil;
import com.atgc.hd.comm.utils.PreferenceUtils;
import com.atgc.hd.comm.utils.UIUtils;
import com.atgc.hd.entity.UploadEntity;
import com.atgc.hd.widget.MyView;
import com.atgc.hd.widget.RoundImageView;
import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    private List<View> viewList;
    private boolean isAddPager = false;
    private UpdateCallback updateCallback;

    public void setUpdateCallback(UpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }

    public PictureAdapter(Context context) {
        this.context = context;
        this.viewList = new ArrayList<>();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_picture2, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        convertView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT, GridView.LayoutParams.MATCH_PARENT));
        convertView.getLayoutParams().width = (int) mImageHeight;
        convertView.getLayoutParams().height = (int) mImageHeight;
        final UploadEntity entity = images.get(position);
        String localPath = entity.getLocalPath();
        if (!localPath.equals("lastItem")) {
            String fileName = FileUtil.getFileName(localPath);
            if (fileName.contains(".mp4") || fileName.contains(".MP4")) {
                holder.ivPlay.setVisibility(View.VISIBLE);
            }
            Glide.with(context).load(localPath).
                    placeholder(R.drawable.zf_default_message_image).
                    crossFade().into(holder.ivPic);
            holder.tvProgress.setVisibility(View.VISIBLE);
            holder.ivBg.setVisibility(View.VISIBLE);
            holder.ivClose.setVisibility(View.GONE);
            holder.tvProgress.setText("开始上传");

            holder.tvProgress.setText("" + entity.getProgress());
            final boolean downloaded = entity.isDownloaded();
            if (!downloaded) {
                if (entity.progress == 0) {
                    entity.setProgress(0);
                    //如果未开始下载，启动异步下载任务
                    MyAsyncTask asyncTask = new MyAsyncTask(context, viewList, entity);
                    //添加THREAD_POOL_EXECUTOR可启动多个异步任务
                    asyncTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR, entity);
                }
            } else {
//            holder.setText("删除");
                holder.tvProgress.setVisibility(View.GONE);
                holder.ivBg.setVisibility(View.GONE);
                holder.ivClose.setVisibility(View.VISIBLE);
            }
        } else {
            holder.tvProgress.setVisibility(View.GONE);
            holder.ivBg.setVisibility(View.GONE);
            holder.ivClose.setVisibility(View.GONE);
            holder.ivPic.setImageResource(R.drawable.compose_pic_add);
        }
        holder.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(entity);
            }
        });
        convertView.setTag(R.id.grid, entity.id);//此处将位置信息作为标识传递
        viewList.add(convertView);
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

    public String getVideoUrls() {
        StringBuilder sb = new StringBuilder();
        if (images != null && images.size() > 1) {
            for (int i = 0; i < images.size() - 1; i++) {
                String path = images.get(i).getUrl();
                String fileName = FileUtil.getFileName(path);
                if (fileName.contains(".mp4") || fileName.contains(".MP4")) {
                    sb.append(path).append(",");
                }
            }
        }
        String url = "";
        String str = sb.toString();
        if (str.contains(",")) {
            url = str.substring(0, str.length() - 1);
        }
        return url;
    }

    public String getImageUrls() {
        StringBuilder sb = new StringBuilder();
        if (images != null && images.size() > 1) {
            for (int i = 0; i < images.size() - 1; i++) {
                String path = images.get(i).getUrl();
                String fileName = FileUtil.getFileName(path);
                if (fileName.contains(".jpg") ||
                        fileName.contains(".png") ||
                        fileName.contains(".jpeg")) {
                    sb.append(path).append(",");
                }
            }
        }
        String url = "";
        String str = sb.toString();
        if (str.contains(",")) {
            url = str.substring(0, str.length() - 1);
        }
        return url;
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

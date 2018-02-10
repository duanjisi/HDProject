/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.client.emergency;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.adapter.BaseRecycleViewAdapter;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.comm.utils.ImageResizer;
import com.atgc.hd.comm.utils.PermissonUtil.PermissionListener;
import com.atgc.hd.comm.utils.PermissonUtil.PermissionUtil;
import com.atgc.hd.comm.utils.UIUtils;
import com.atgc.hd.entity.VideoEntity;
import com.atgc.hd.widget.RecyclingImageView;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johnny on 2017/6/26.
 * 本地视频文件
 */
public class LocalVideoFilesActivity extends BaseActivity {

    //private GridView gv_video;
    private ImageAdapter mAdapter;
    private List<VideoEntity> mList;
    private ImageResizer mImageResizer;
    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private RecyclerView rv_video;
    private RImageAdapter rImageAdapter;
    private PermissionListener permissionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        //6.0以下系统，取消请求权限
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            initView();
        } else {
            getPermission();
        }
    }

    @Override
    public String toolBarTitle() {
        return getString(R.string.local_videos);
    }

    private void getPermission() {
        permissionListener = new PermissionListener() {
            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
                PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
            }

            @Override
            public void onRequestPermissionSuccess() {
                initView();
            }

            @Override
            public void onRequestPermissionError() {
                showToast("请给予定位权限");
            }
        };
        PermissionUtil
                .with(this)
                .permissions(
                        PermissionUtil.PERMISSIONS_SD_READ_WRITE //
                ).request(permissionListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionUtil.onRequestPermissionsResult(this, requestCode, permissions, permissionListener);
    }

    private void initView() {
        barHelper.setTitleColor(getResources().getColor(R.color.white));
        rv_video = (RecyclerView) findViewById(R.id.rv_video);
//        iv_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//        tv_title = (TextView) findViewById(R.id.tv_title);
//        tv_title.setText("选择本地视频");
        rv_video.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        mList = new ArrayList<>();
        rImageAdapter = new RImageAdapter(this);
        rv_video.addItemDecoration(new MyItemDecoration());
        rv_video.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position >= mList.size()) {
                    return;
                }
                VideoEntity vEntty = mList.get(position);
                Log.i("pos:", "" + position);
                if (vEntty != null) {
                    String filePath = vEntty.filePath;
                    String img = vEntty.img;
                    Log.i("info", "================img:" + img);
                    Intent intent = new Intent();
                    intent.putExtra("filePath", filePath);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }

            @Override
            public void onLongClick(View view, int posotion) {

            }
        }));
        rv_video.setAdapter(rImageAdapter);
        new SearchThead().start();
    }

    private void getVideoFile() {
        ContentResolver mContentResolver = getContentResolver();
        Cursor cursor = mContentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.DEFAULT_SORT_ORDER);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // ID:MediaStore.Audio.Media._ID
                int id = cursor.getInt(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media._ID));

                // title：MediaStore.Audio.Media.TITLE
                String title = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                // path：MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                String mineType = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                String magic = cursor.getString(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.MINI_THUMB_MAGIC));
                // duration：MediaStore.Audio.Media.DURATION
                int duration = cursor
                        .getInt(cursor
                                .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                // 大小：MediaStore.Audio.Media.SIZE
                int size = (int) cursor.getLong(cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
//                && duration <= 30 * 1000
                if (size > 0 && size <= 1024 * 1024 * 35 &&
                        (url.endsWith(".mp4") || url.endsWith(".MP4")) &&
                        duration <= 5 * 60 * 1000) {
                    VideoEntity entty = new VideoEntity();
                    entty.ID = id;
                    entty.title = title;
                    entty.filePath = url;
                    entty.duration = duration;
                    entty.size = size;
                    entty.img = magic;
                    mList.add(entty);
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
            mHandler.sendEmptyMessage(1);
            cursor = null;
        }
    }

    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private RelativeLayout.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new RelativeLayout.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return (position == 0) ? null : mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.em_choose_griditem, container, false);
                holder.imageView = (RecyclingImageView) convertView.findViewById(R.id.imageView);
                holder.icon = (ImageView) convertView.findViewById(R.id.video_icon);
                holder.tvDur = (TextView) convertView.findViewById(R.id.chatting_length_iv);
                holder.tvSize = (TextView) convertView.findViewById(R.id.chatting_size_iv);
                holder.imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.imageView.setLayoutParams(mImageViewLayoutParams);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (holder.imageView.getLayoutParams().height != mItemHeight) {
                holder.imageView.setLayoutParams(mImageViewLayoutParams);
            }

            holder.icon.setVisibility(View.VISIBLE);
            if (mList != null && position < mList.size()) {
                VideoEntity entty = mList.get(position);
                holder.tvDur.setVisibility(View.VISIBLE);

                holder.tvDur.setText(toTime(entty.duration));
                holder.tvSize.setText(getDataSize(entty.size));
                //holder.imageView.setImageResource(R.drawable.em_empty_photo);
                //mImageResizer.loadImage(entty.filePath, holder.imageView);
                Picasso.with(context).load(entty.filePath).error(R.drawable.em_empty_photo).into(holder.imageView);
            }
            return convertView;
        }

        /**
         * Sets the item height. Useful for when we know the column width so the
         * height can be set to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new RelativeLayout.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT, mItemHeight);
            mImageResizer.setImageSize(height);
            notifyDataSetChanged();
        }

        class ViewHolder {
            RecyclingImageView imageView;
            ImageView icon;
            TextView tvDur;
            TextView tvSize;
        }
    }

    private class RImageAdapter extends BaseRecycleViewAdapter {
        private Context mContext;

        public RImageAdapter(Context context) {
            mContext = context;
        }

        @Override
        public void onBindItemHolder(RecyclerView.ViewHolder holder, int position) {
            final ViewHolder holder1 = (ViewHolder) holder;

            holder1.icon.setVisibility(View.VISIBLE);
            if (mList != null && position < mList.size()) {
                VideoEntity entty = mList.get(position);
                holder1.tvDur.setVisibility(View.VISIBLE);
                holder1.tvDur.setText(toTime(entty.duration));
                holder1.tvSize.setText(getDataSize(entty.size));
                Glide.with(context).load(entty.filePath).error(R.drawable.em_empty_photo).into(holder1.imageView);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_video_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return mList == null ? 0 : mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImageView icon;
            TextView tvDur;
            TextView tvSize;
            FrameLayout fl_p;

            public ViewHolder(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.imageView);
                icon = (ImageView) itemView.findViewById(R.id.video_icon);
                tvDur = (TextView) itemView.findViewById(R.id.chatting_length_iv);
                tvSize = (TextView) itemView.findViewById(R.id.chatting_size_iv);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                int mh = UIUtils.getScreenWidth(mContext) / 2 - 8;
                Log.i("mh", ":" + mh);
                imageView.getLayoutParams().height = mh;
                imageView.getLayoutParams().width = mh;
            }
        }
    }

    public String toTime(int var0) {
        var0 /= 1000;
        int var1 = var0 / 60;
        boolean var2 = false;
        if (var1 >= 60) {
            int var4 = var1 / 60;
            var1 %= 60;
        }

        int var3 = var0 % 60;
        return String.format("%02d:%02d", new Object[]{Integer.valueOf(var1), Integer.valueOf(var3)});
    }

    public String getDataSize(long var0) {
        DecimalFormat var2 = new DecimalFormat("###.00");
        return var0 < 1024L ? var0 + "bytes" : (var0 < 1048576L ? var2.format((double) ((float) var0 / 1024.0F)) + "KB" : (var0 < 1073741824L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F)) + "MB" : (var0 < 0L ? var2.format((double) ((float) var0 / 1024.0F / 1024.0F / 1024.0F)) + "GB" : "error")));
    }

    class MyItemDecoration extends RecyclerView.ItemDecoration {
        /**
         * @param outRect 边界
         * @param view    recyclerView ItemView
         * @param parent  recyclerView
         * @param state   recycler 内部数据管理
         */
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            //设定底部边距为1px
            outRect.set(2, 0, 2, 1);
        }
    }

    class SearchThead extends Thread {
        @Override
        public void run() {
            getVideoFile();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1 && mList != null) {
                rImageAdapter.notifyDataSetChanged();
//                mAdapter = new ImageAdapter(VideoListActivity.this);
//                gv_video.setAdapter(mAdapter);
//                gv_video.getViewTreeObserver().addOnGlobalLayoutListener(
//                        new ViewTreeObserver.OnGlobalLayoutListener() {
//                            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//                            @Override
//                            public void onGlobalLayout() {
//                                final int numColumns = (int) Math.floor(gv_video
//                                        .getWidth()
//                                        / (mImageThumbSize + mImageThumbSpacing));
//                                if (numColumns > 0) {
//                                    final int columnWidth = (gv_video.getWidth() / numColumns)
//                                            - mImageThumbSpacing;
//                                    mAdapter.setItemHeight(columnWidth);
//
//                                    if (Utils.hasJellyBean()) {
//                                        gv_video.getViewTreeObserver()
//                                                .removeOnGlobalLayoutListener(this);
//                                    } else {
//                                        gv_video.getViewTreeObserver()
//                                                .removeGlobalOnLayoutListener(this);
//                                    }
//                                }
//                            }
//                        });
            }
        }
    };

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private View childView;
        private RecyclerView touchView;

        public RecyclerItemClickListener(Context context, final OnItemClickListener mListener) {
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent ev) {
                    if (childView != null && mListener != null) {
                        mListener.onItemClick(childView, touchView.getChildPosition(childView));
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent ev) {
                    if (childView != null && mListener != null) {
                        mListener.onLongClick(childView, touchView.getChildPosition(childView));
                    }
                }
            });
        }

        GestureDetector mGestureDetector;

        @Override
        public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
            mGestureDetector.onTouchEvent(motionEvent);
            childView = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
            touchView = recyclerView;
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

        public interface OnItemClickListener {
            public void onItemClick(View view, int position);

            public void onLongClick(View view, int posotion);
        }
    }
}

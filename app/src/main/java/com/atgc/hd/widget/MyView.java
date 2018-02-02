/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.widget;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.comm.net.http.MyTask;
import com.atgc.hd.comm.net.http.UploadTask;
import com.bumptech.glide.Glide;


/**
 * <p>描述：
 * <p>作者：duanjisi 2018年 01月 22日
 */

public class MyView extends RelativeLayout implements UploadTask.updateProgressCallback {
    private ImageView ivClose;
    private RoundImageView ivPic;
    private RoundImageView ivBg;
    private TextView tvProgress;
    private UploadTask task;
    private Context context;
    public boolean isLoading = false;
    public boolean loaded = false;
    private callback callback;
    private Handler handler = new Handler();

    public void setCallback(MyView.callback callback) {
        this.callback = callback;
    }

    public void setCloseListener(View.OnClickListener closeListener) {
        this.ivClose.setOnClickListener(closeListener);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.item_picture2, this);
        this.ivClose = findViewById(R.id.iv_close);
        this.ivPic = findViewById(R.id.iv_image);
        this.ivBg = findViewById(R.id.iv_bg);
        this.tvProgress = findViewById(R.id.tv_progress);
    }


    public MyView(Context context) {
        super(context);
        this.context = context;
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.item_picture2, this);
        this.ivClose = findViewById(R.id.iv_close);
        this.ivPic = findViewById(R.id.iv_image);
        this.ivBg = findViewById(R.id.iv_bg);
        this.tvProgress = findViewById(R.id.tv_progress);
    }

    public void startUpload(String path) {
        if (path.equals("lastItem")) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    tvProgress.setVisibility(View.GONE);
                    ivBg.setVisibility(View.GONE);
                    ivClose.setVisibility(View.GONE);
                    ivPic.setImageResource(R.drawable.btn_photo);
                }
            });
        } else {
            Glide.with(context).load(path).
                    placeholder(R.drawable.zf_default_message_image).
                    crossFade().into(ivPic);
            if (!loaded) {
                if (!isLoading) {
                    tvProgress.setVisibility(View.VISIBLE);
                    ivBg.setVisibility(View.VISIBLE);
                    ivClose.setVisibility(View.GONE);
                    tvProgress.setText("开始上传");
                    task = new UploadTask(context, path);
                    task.setCallback(this);
                    new Thread(task).start();
                }
            } else {
                tvProgress.setVisibility(View.GONE);
                ivBg.setVisibility(View.GONE);
                ivClose.setVisibility(View.GONE);
            }
        }
    }

    public interface callback {
        void getFileUrl(String url);
    }

    @Override
    public void onPostExecute(String string) {
        loaded = true;
        tvProgress.setVisibility(View.GONE);
        ivBg.setVisibility(View.GONE);
        ivClose.setVisibility(View.VISIBLE);
        if (callback != null) {
            callback.getFileUrl(string);
        }
    }

    @Override
    public void onProgressUpdate(int progress) {
        isLoading = true;
        tvProgress.setText("   " + progress + "%   " + "\n" + "正在上传");
    }

}

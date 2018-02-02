/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.comm.net.http.UploadTask;
import com.atgc.hd.comm.utils.FileUtil;
import com.bumptech.glide.Glide;

import java.util.HashMap;


/**
 * <p>描述：
 * <p>作者：duanjisi 2018年 01月 22日
 */

public class RoundView extends RelativeLayout {
    private ImageView ivPlay;
    private RoundImageView ivPic;
    private Context context;
    private Handler handler = new Handler();

    public RoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.item_picture3, this);
        this.ivPlay = findViewById(R.id.iv_play);
        this.ivPic = findViewById(R.id.iv_image);
    }


    public RoundView(Context context) {
        super(context);
        this.context = context;
        // 加载布局
        LayoutInflater.from(context).inflate(R.layout.item_picture3, this);
        this.ivPlay = findViewById(R.id.iv_play);
        this.ivPic = findViewById(R.id.iv_image);
    }

    public void showImage(final String path) {
        if (!TextUtils.isEmpty(path)) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String fileName = FileUtil.getFileName(path);
                    if (fileName.contains(".mp4") || fileName.contains(".MP4")) {
                        ivPlay.setVisibility(VISIBLE);
                        Bitmap bitmap = createVideoThumbnail(path, 1);
                        if (bitmap != null) {
                            ivPic.setImageBitmap(bitmap);
                        }
                    } else {
                        Glide.with(context).load(path).
                                placeholder(R.drawable.zf_default_message_image).
                                crossFade().into(ivPic);
                    }
                }
            });
        }
    }


    //获取视频缩略图
    private Bitmap createVideoThumbnail(String url, int type) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //将网络文件以及本地文件区分开来设置
            if (type == 1) {
                retriever.setDataSource(url, new HashMap<String, String>());
            } else if (type == 0) {
                retriever.setDataSource(url);
            }
            bitmap = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_NEXT_SYNC);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException ex) {
                ex.printStackTrace();
            }
        }
        return bitmap;
    }
}

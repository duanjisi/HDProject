/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.client.emergency;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.adapter.PicAdapter;
import com.atgc.hd.adapter.PictureAdapter;
import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.client.player.VideoPlayerActivity;
import com.atgc.hd.comm.utils.FileUtil;
import com.atgc.hd.comm.utils.UIUtils;
import com.atgc.hd.entity.EventEntity;
import com.atgc.hd.entity.UploadEntity;
import com.atgc.hd.widget.MyGridView;
import com.atgc.hd.widget.RoundView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * <p>描述：事件详情
 * <p>作者：duanjisi 2018年 02月 01日
 */

public class EmergencyDetailsActivity2 extends BaseActivity {

    private PicAdapter pictureAdapter;
    @BindView(R.id.tv_place)
    TextView tvPlace;
    @BindView(R.id.tv_event)
    TextView tvEvent;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.grid)
    MyGridView gridView;
    private int mImageHeight;
    private EventEntity eventEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_details2);
        ButterKnife.bind(this);
        barHelper.setTitleColor(getResources().getColor(R.color.white));
        initData();
        bindData();
    }


    private void initData() {
        mImageHeight = (UIUtils.getScreenWidth(context) - UIUtils.dip2px(context, 60)) / 4;
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            eventEntity = (EventEntity) bundle.getSerializable("obj");
        }
    }


    private void bindData() {
        if (eventEntity != null) {
            tvPlace.setText("地点：" + eventEntity.getPlace());
            tvEvent.setText("事件:" + eventEntity.getDescription());
            tvTime.setText(eventEntity.getTime());
            showView(eventEntity.getUrls());
        }
    }


    private ArrayList<String> images = new ArrayList<>();

    private void showView(String urls) {
        pictureAdapter = new PicAdapter(context);
        gridView.setAdapter(pictureAdapter);
        gridView.setOnItemClickListener(new itemClickListener());

        if (!TextUtils.isEmpty(urls)) {
            ArrayList<UploadEntity> entities = new ArrayList<>();
            String[] strs = urls.split(",");
            for (int i = 0; i < strs.length; i++) {
                String url = strs[i];
                if (url.contains(".jpg") || url.contains(".mp4")) {
                    images.add(url);
//                    if (url.contains(".jpg")) {
//                        images.add(url);
//                    }
//                    llImage.addView(insertImage(url, i));

//                    objId++;
//                    UploadEntity entity = new UploadEntity();
//                    entity.setUrl(url);
//                    entity.id = objId;
//                    entities.add(entity);
                }
            }
            pictureAdapter.addDatas(images);
        }
    }

    private int objId = 0;

    private void upLoadFile(final String path) {
        objId++;
        UploadEntity entity = new UploadEntity();
        entity.setLocalPath(path);
        entity.id = objId;
    }

    private class itemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            String url = (String) adapterView.getItemAtPosition(position);
//            String url = entity.getUrl();
            if (!TextUtils.isEmpty(url)) {
                String fileName = FileUtil.getFileName(url);
                if (fileName.contains(".jpg")) {
                    ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                    ImagePagerActivity.startImagePagerActivity(context, images, position, imageSize, false);
                } else {
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("videoPath", url);
                    intent.putExtra("imgurl", url);
                    startActivity(intent);
                }
            }
        }
    }

    private View insertImage(String url, int position) {
        final LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(mImageHeight, mImageHeight));
        layout.setGravity(Gravity.CENTER);
        layout.setTag(url);
        RoundView child = new RoundView(context);
        child.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        child.getLayoutParams().width = mImageHeight;
        child.getLayoutParams().height = mImageHeight;
        child.showImage(url);
        layout.addView(child);
        layout.setOnClickListener(new onClickListener());
        layout.setTag(R.id.tag_position, position);
        return layout;
    }


    private class onClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            String url = (String) view.getTag();
            int position = (int) view.getTag(R.id.tag_position);
            String fileName = FileUtil.getFileName(url);
            if (fileName.contains(".jpg")) {
                ImagePagerActivity.ImageSize imageSize = new ImagePagerActivity.ImageSize(view.getMeasuredWidth(), view.getMeasuredHeight());
                ImagePagerActivity.startImagePagerActivity(context, images, position, imageSize, false);
            } else {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("videoPath", url);
                intent.putExtra("imgurl", url);
                startActivity(intent);
            }
        }
    }

    @Override
    public String toolBarTitle() {
        return getString(R.string.event_details);
    }
}

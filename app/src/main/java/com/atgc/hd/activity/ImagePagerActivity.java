/**
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.atgc.hd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atgc.hd.base.BaseActivity;
import com.atgc.hd.comm.utils.FileUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.atgc.hd.R;
import com.atgc.hd.widget.ActionSheet;
import com.atgc.hd.widget.HackyViewPager;
import com.atgc.hd.widget.photoview.PhotoView;
import com.atgc.hd.widget.photoview.PhotoViewAttacher;

/**
 * Created by GMARUnity on 2017/2/3.
 */
public class ImagePagerActivity extends Activity implements ActionSheet.OnSheetItemClickListener {

    public static final String INTENT_IMGURLS = "imgurls";
    public static final String INTENT_POSITION = "position";
    public static final String INTENT_IMAGESIZE = "imagesize";

    private List<View> guideViewList = new ArrayList<View>();
    private LinearLayout guideGroup;
    public ImageSize imageSize;
    private int startPos;
    private ArrayList<String> imgUrls;
    private Bitmap bitmap;
    private PhotoView pv_view;
    private boolean isDelete = false;
    private ImageAdapter mAdapter;
    private RelativeLayout rl_title;
    private TextView tv_back, tv_right, tv_indicator;
    private HackyViewPager viewPager;

    public static void startImagePagerActivity(Context context, List<String> imgUrls, int position, ImageSize imageSize, boolean isDelete) {
        Intent intent = new Intent(context, ImagePagerActivity.class);
        intent.putStringArrayListExtra(INTENT_IMGURLS, new ArrayList<>(imgUrls));
        intent.putExtra(INTENT_POSITION, position);
        intent.putExtra(INTENT_IMAGESIZE, imageSize);
        intent.putExtra("isdelete", isDelete);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle_imagepager);
        tv_indicator = (TextView) findViewById(R.id.tv_indicator);
        viewPager = (HackyViewPager) findViewById(R.id.pager);
        guideGroup = (LinearLayout) findViewById(R.id.guideGroup);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("lits", imgUrls);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        tv_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showActionSheet();
            }
        });
        getIntentData();

        mAdapter = new ImageAdapter(this);
        mAdapter.setDatas(imgUrls);
        mAdapter.setImageSize(imageSize);
        viewPager.setAdapter(mAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < guideViewList.size(); i++) {
                    guideViewList.get(i).setSelected(i == position ? true : false);
                }
                int curpos = position + 1;
                tv_indicator.setText(curpos + "/" + mAdapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(startPos);

        addGuideView(guideGroup, startPos, imgUrls);

    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            startPos = intent.getIntExtra(INTENT_POSITION, 0);
            imgUrls = intent.getStringArrayListExtra(INTENT_IMGURLS);
            imageSize = (ImageSize) intent.getSerializableExtra(INTENT_IMAGESIZE);
            this.isDelete = intent.getBooleanExtra("isdelete", false);
            if (isDelete) {
                rl_title.setVisibility(View.VISIBLE);
            }
            if (imgUrls != null) {
                if (imgUrls.size() <= 1) {
                    tv_indicator.setVisibility(View.GONE);
                } else {
                    tv_indicator.setVisibility(View.VISIBLE);
                    tv_indicator.setText("1/" + imgUrls.size());
                }
            }
        }
    }

    private void addGuideView(LinearLayout guideGroup, int startPos, ArrayList<String> imgUrls) {
        if (imgUrls != null && imgUrls.size() > 0) {
            guideViewList.clear();
            for (int i = 0; i < imgUrls.size(); i++) {
                View view = new View(this);
                view.setBackgroundResource(R.drawable.selector_circle_guide_bg);
                view.setSelected(i == startPos ? true : false);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.gudieview_heigh),
                        getResources().getDimensionPixelSize(R.dimen.gudieview_heigh));
                layoutParams.setMargins(10, 0, 0, 0);
                guideGroup.addView(view, layoutParams);
                guideViewList.add(view);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private class ImageAdapter extends PagerAdapter {

        private List<String> datas = new ArrayList<String>();
        private LayoutInflater inflater;
        private Context context;
        private ImageSize imageSize;
        private ImageView smallImageView = null;

        public void setDatas(List<String> datas) {
            if (datas != null)
                this.datas = datas;
        }

        public void setImageSize(ImageSize imageSize) {
            this.imageSize = imageSize;
        }

        public ImageAdapter(Context context) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            if (datas == null) return 0;
            return datas.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View view = inflater.inflate(R.layout.item_pager_image, container, false);
            if (view != null) {
                pv_view = (PhotoView) view.findViewById(R.id.iv_icon);
                pv_view.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                    @Override
                    public void onPhotoTap(View view, float x, float y) {
                        if (!isDelete) {
                            finish();
                        }
                    }
                });
                pv_view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (!isDelete) {
                            showActionSheet();
                        }
                        return false;
                    }
                });
                if (imageSize != null) {
                    //预览imageView
                    smallImageView = new ImageView(context);
                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imageSize.getWidth(), imageSize.getHeight());
                    layoutParams.gravity = Gravity.CENTER;
                    smallImageView.setLayoutParams(layoutParams);
                    smallImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    ((LinearLayout) view).addView(smallImageView);
                }

                //loading
                final ProgressBar loading = new ProgressBar(context);
                FrameLayout.LayoutParams loadingLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                loadingLayoutParams.gravity = Gravity.CENTER;
                loading.setLayoutParams(loadingLayoutParams);
                ((LinearLayout) view).addView(loading);

                final String imgurl = datas.get(position);
                Glide.with(context)
                        .load(imgurl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存多个尺寸
                        .thumbnail(0.1f)//先显示缩略图  缩略图为原图的1/10
                        .error(R.drawable.ic_launcher)
                        .into(new GlideDrawableImageViewTarget(pv_view) {
                            @Override
                            public void onLoadStarted(Drawable placeholder) {
                                super.onLoadStarted(placeholder);
                               /* if(smallImageView!=null){
                                    smallImageView.setVisibility(View.VISIBLE);
                                    Glide.with(context).load(imgurl).into(smallImageView);
                                }*/
                                loading.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                super.onLoadFailed(e, errorDrawable);
                                /*if(smallImageView!=null){
                                    smallImageView.setVisibility(View.GONE);
                                }*/
                                loading.setVisibility(View.GONE);
                            }

                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                super.onResourceReady(resource, animation);
                                loading.setVisibility(View.GONE);
                                /*if(smallImageView!=null){
                                    smallImageView.setVisibility(View.GONE);
                                }*/
                            }
                        });

                container.addView(view, 0);
            }
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }


    }

    @Override
    protected void onDestroy() {
        guideViewList.clear();
        super.onDestroy();
    }

    public static class ImageSize implements Serializable {

        private int width;
        private int height;

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }
    }

    private void showActionSheet() {
        ActionSheet actionSheet = new ActionSheet(ImagePagerActivity.this)
                .builder()
                .setCancelable(false)
                .setCanceledOnTouchOutside(true);
        if (isDelete) {
            actionSheet.setTitle("要删除这张照片吗");
            actionSheet.addSheetItem("删除", ActionSheet.SheetItemColor.Red,
                    ImagePagerActivity.this);
        } else {
            actionSheet.addSheetItem(getString(R.string.save_photo), ActionSheet.SheetItemColor.Black,
                    ImagePagerActivity.this);
        }
        actionSheet.show();
    }

    @Override
    public void onClick(int which) {
        if (isDelete) {
            int page = viewPager.getCurrentItem();
            if (imgUrls != null) {
                if (imgUrls.size() > 1) {
                    if (imgUrls.size() > page) {
                        imgUrls.remove(page);
                        mAdapter.notifyDataSetChanged();
                        tv_indicator.setText(1 + "/" + mAdapter.getCount());
                    }
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("lits", null);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        } else {
            if (pv_view != null) {
                pv_view.setDrawingCacheEnabled(true);
                Bitmap bitmap = pv_view.getDrawingCache();
                FileUtil.saveImageToGallery(this, bitmap);
                pv_view.setDrawingCacheEnabled(false);
                showToast(getString(R.string.success_save));
            } else {
                showToast(getString(R.string.msg_could_not_save_photo));
            }
        }
    }


    private void showToast(String str) {
        if (!TextUtils.isEmpty(str)) {
            Toast.makeText(ImagePagerActivity.this, str, Toast.LENGTH_LONG).show();
        }
    }
}

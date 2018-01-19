package com.atgc.hd.comm.utils;

import android.content.Context;
import android.graphics.Bitmap;

import com.atgc.hd.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * <p>描述：图片加载工具
 * <p>作者：duanjisi 2018年 01月 17日
 */
public class ImageLoaderUtils {
    private static final Object temoObject = new Object();
    private static final int cacheSize = 1024 * 1024 * 100;//100M

    /**
     * 初始化ImageLoader
     *
     * @param context
     */
    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                //.discCacheFileNameGenerator(new Md5FileNameGeneratorEx())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheSize(cacheSize);
        ImageLoaderConfiguration config = builder.build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    /**
     * 获取ImagerLoader对象
     *
     * @return ImagerLoader对象
     */
    public static ImageLoader createImageLoader(Context context) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        if (imageLoader.isInited()) {
            return imageLoader;
        }
        // 避免多线程对其二次初始化
        synchronized (temoObject) {
            if (imageLoader.isInited()) {
                return imageLoader;
            }
            initImageLoader(context);
            return imageLoader;
        }
    }

    /**
     * 获取显示图像的配置
     *
     * @return
     */
    public static DisplayImageOptions getDisplayImageOptions() {
        return getDisplayImageOptions(R.drawable.zf_default_message_image);
    }

    public static DisplayImageOptions getIconImageOptions() {
        return getDisplayImageOptions(R.drawable.zf_default_message_image);
    }

    public static DisplayImageOptions getDisplayScaleImageOptions() {
        return new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
                .showImageForEmptyUri(R.drawable.zf_default_message_image)
                .showImageOnFail(R.drawable.zf_default_message_image)
                .showStubImage(R.drawable.zf_default_message_image)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    /**
     * 根据传入的默认图像获取显示图像的配置
     *
     * @param defaultPicRes 默认图像的资源id
     * @return
     */
    public static DisplayImageOptions getDisplayImageOptions(int defaultPicRes) {
        return new DisplayImageOptions.Builder().cacheInMemory().cacheOnDisc()
                .showImageForEmptyUri(defaultPicRes).showImageOnFail(defaultPicRes)
                .showStubImage(defaultPicRes).bitmapConfig(Bitmap.Config.RGB_565).build();
    }
}

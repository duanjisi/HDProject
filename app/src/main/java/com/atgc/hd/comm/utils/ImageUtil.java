package com.atgc.hd.comm.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.graphics.drawable.VectorDrawableCompat;

/**
 * <p>描述：
 * <p>作者： liangguokui 2018/1/17
 */
public class ImageUtil {

    private ImageUtil() {
    }

    /**
     * 将svg格式的xml图标转换成drawable
     *
     * @param context
     * @param drawableId
     * @return
     */
    public static Drawable getDrawableFromVectorDrawable(Context context, int drawableId) {

        Drawable drawable = VectorDrawableCompat.create(context.getResources(), drawableId, null);

        return drawable;
    }

}

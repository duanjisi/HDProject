package com.atgc.hd.client.widgethelper;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.comm.widget.VectorCompatTextView;

/**
 * <p>描述： 自定义的toolbar帮助类
 * <p>作者： liangguokui 2018/1/17
 */
public class BarHelper {

    private Context context;

    private View barView;

    private VectorCompatTextView tvActionLeft;
    private VectorCompatTextView tvActionRight;
    private TextView tvTitle;

    public BarHelper(Context context, View barView) {
        this.context = context;
        this.barView = barView;

        init();
    }

    private void init() {
        tvActionLeft = findById(R.id.tv_left);
        tvActionRight = findById(R.id.tv_right);
        tvTitle = findById(R.id.tv_title);
    }

    /**
     * 设置标题
     *
     * @param title
     * @return
     */
    public BarHelper setTitle(String title) {
        tvTitle.setText(title);
        return this;
    }

    /**
     * 设置标题颜色
     *
     * @param color
     * @return
     */
    public BarHelper setTitleColor(int color) {
        tvTitle.setTextColor(color);
        return this;
    }

    /**
     * 设置左边按钮文字
     *
     * @param character
     * @return
     */
    public BarHelper setActionLeftText(Character character) {
        tvActionLeft.setText(character);
        return this;
    }

    /**
     * 设置左边按钮文字颜色
     *
     * @param color
     * @return
     */
    public BarHelper setActionLeftTextColor(int color) {
        tvActionLeft.setTextColor(color);
        return this;
    }

    /**
     * 设置右边按钮文字
     *
     * @param character
     * @return
     */
    public BarHelper setActionRightText(String character) {
        tvActionRight.setText(character);
        return this;
    }

    /**
     * 设置右边按钮文字颜色
     *
     * @param color
     * @return
     */
    public BarHelper setActionRightTextColor(int color) {
        tvActionRight.setTextColor(color);
        return this;
    }

    /**
     * 设置左边按钮图标
     *
     * @param drawableResourceId
     * @return
     */
    public BarHelper setActionLeftDrawable(int drawableResourceId) {
        if (drawableResourceId == -1) {
            tvActionLeft.setCompoundDrawablesWithIntrinsicBounds(drawableResourceId, 0, 0, 0);
        } else {
            tvActionLeft.setCompoundDrawablesWithIntrinsicBounds(drawableResourceId, 0, 0, 0);
        }
        return this;
    }

    /**
     * 设置右边按钮图标
     *
     * @param drawableResourceId
     * @return
     */
    public BarHelper setActionRightDrawable(int drawableResourceId) {
        if (drawableResourceId == -1) {
            tvActionRight.setCompoundDrawablesWithIntrinsicBounds(drawableResourceId, 0, 0, 0);
        } else {
            tvActionRight.setCompoundDrawablesWithIntrinsicBounds(drawableResourceId, 0, 0, 0);
        }
        return this;
    }

    /**
     * 设置整个bar的背景色
     *
     * @param color
     * @return
     */
    public BarHelper setBackgroundColor(int color) {
        barView.setBackgroundColor(color);
        return this;
    }

    /**
     * @param listener
     * @return
     */
    public BarHelper setActionLeftListener(View.OnClickListener listener) {
        tvActionLeft.setOnClickListener(listener);
        return this;
    }

    /**
     * @param listener
     * @return
     */
    public BarHelper setActionRightListener(View.OnClickListener listener) {
        tvActionRight.setOnClickListener(listener);
        return this;
    }

    /**
     * 左边按钮的显示/隐藏
     * @param display
     */
    public void displayActionLeft(boolean display) {
        if (display) {
            tvActionLeft.setVisibility(View.VISIBLE);
        } else {
            tvActionLeft.setVisibility(View.GONE);
        }
    }

    /**
     * 右边按钮的显示/隐藏
     * @param display
     */
    public void displayActionRight(boolean display) {
        if (display) {
            tvActionRight.setVisibility(View.VISIBLE);
        } else {
            tvActionRight.setVisibility(View.GONE);
        }
    }

    /**
     * actionbar的显示/隐藏
     * @param display
     */
    public void displayActionBar(boolean display) {
        if (display) {
            barView.setVisibility(View.VISIBLE);
        } else {
            barView.setVisibility(View.GONE);
        }
    }

    private <T extends View> T findById(int id) {
        return (T) barView.findViewById(id);
    }
}

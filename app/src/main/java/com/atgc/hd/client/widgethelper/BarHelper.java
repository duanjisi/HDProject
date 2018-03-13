package com.atgc.hd.client.widgethelper;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.TextView;

import com.atgc.hd.R;
import com.atgc.hd.comm.widget.VectorCompatTextView;
import com.atgc.hd.comm.widget.popupwindow.ExtendPopupWindow;
import com.atgc.hd.comm.widget.popupwindow.PopupItem;

import java.util.ArrayList;
import java.util.List;

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

    private List<MenuEntity> rightActions;

    public BarHelper(Context context, View barView) {
        this.context = context;
        this.barView = barView;

        init();
    }

    private void init() {
        tvActionLeft = findById(R.id.tv_left);
        tvActionRight = findById(R.id.tv_right);
        tvTitle = findById(R.id.tv_title);

        rightActions = new ArrayList<>();
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

    public BarHelper addLeftAction(MenuEntity entity) {
        tvActionLeft.setText(entity.menuTitle);
        tvActionLeft.setTextColor(entity.actionTextColor);
        tvActionLeft.setCompoundDrawablesWithIntrinsicBounds(entity.actionIconResourceId, 0, 0, 0);
        tvActionLeft.setOnClickListener(entity.actionListener);
        return this;
    }

    /**
     * 可调用多次添加多个action
     *
     * @param entity
     * @return
     */
    public BarHelper addRightAction(MenuEntity entity) {
        rightActions.add(entity);

        if (rightActions.size() > 1) {
            tvActionRight.setText("");
            tvActionRight.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_more, 0, 0, 0);
            tvActionRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showRightActions(v);
                }
            });
        } else {
            tvActionRight.setText(entity.menuTitle);
            tvActionRight.setTextColor(entity.actionTextColor);
            tvActionRight.setCompoundDrawablesWithIntrinsicBounds(entity.actionIconResourceId, 0, 0, 0);
            tvActionRight.setOnClickListener(entity.actionListener);
        }

        return this;
    }

    private ExtendPopupWindow menuPopupWindown;
    private void showRightActions(View view) {
        if (menuPopupWindown == null) {
            menuPopupWindown = new ExtendPopupWindow(context, getMoreMenuItems(), acitonItemListener);
        }

        menuPopupWindown.notifyData();
        menuPopupWindown.show(view);
    }

    private List<PopupItem> getMoreMenuItems() {
        List<PopupItem> itemList = new ArrayList<>();
        for (int i = 0; i < rightActions.size(); i++) {
            MenuEntity entity = rightActions.get(i);
            itemList.add(new PopupItem(i, entity.menuTitle));
        }
        return itemList;
    }

    private ExtendPopupWindow.MenuItemClickListener acitonItemListener = new ExtendPopupWindow.MenuItemClickListener() {
        @Override
        public void onItemClick(int position, PopupItem item) {
            MenuEntity menuEntity = rightActions.get(position);
            if (menuEntity.actionListener == null) {
            } else {
                menuEntity.actionListener.onClick(null);
            }
        }
    };

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
        tvActionLeft.setCompoundDrawablesWithIntrinsicBounds(drawableResourceId, 0, 0, 0);
        return this;
    }

    /**
     * 设置右边按钮图标
     *
     * @param drawableResourceId
     * @return
     */
    public BarHelper setActionRightDrawable(int drawableResourceId) {
        tvActionRight.setCompoundDrawablesWithIntrinsicBounds(drawableResourceId, 0, 0, 0);
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
     *
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
     *
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
     *
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

    public static class MenuEntity {
        private String menuTitle;
        private int actionTextColor;
        private int actionIconResourceId;
        private View.OnClickListener actionListener;

        /**
         * 设置按钮文字
         *
         * @param menuTitle
         * @return
         */
        public MenuEntity setMenuTitle(String menuTitle) {
            this.menuTitle = menuTitle;
            return this;
        }

        /**
         * 设置按钮文字颜色
         *
         * @param actionTextColor
         * @return
         */
        public MenuEntity setActionTextColor(int actionTextColor) {
            this.actionTextColor = actionTextColor;
            return this;
        }

        /**
         * 设置按钮图标
         *
         * @param actionIconResourceId
         * @return
         */
        public MenuEntity setActionDrawable(@DrawableRes int actionIconResourceId) {
            this.actionIconResourceId = actionIconResourceId;
            return this;
        }

        /**
         * @param actionListener
         * @return
         */
        public MenuEntity setActionRightListener(View.OnClickListener actionListener) {
            this.actionListener = actionListener;
            return this;
        }
    }
}

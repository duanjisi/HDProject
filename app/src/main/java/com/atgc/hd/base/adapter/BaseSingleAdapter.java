package com.atgc.hd.base.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.atgc.hd.base.adapter.holder.ViewHolder;
import com.atgc.hd.base.adapter.interfaces.OnItemChildClickListener;
import com.atgc.hd.base.adapter.interfaces.OnItemClickListener;
import com.atgc.hd.base.adapter.interfaces.OnSwipeMenuClickListener;

import java.util.ArrayList;

/**
 * Author: Othershe
 * Time: 2016/9/9 15:52
 */
public abstract class BaseSingleAdapter<T> extends BaseAdapter<T> {
    private OnItemClickListener<T> mItemClickListener;

    private ArrayList<Integer> mViewId = new ArrayList<>();
    private ArrayList<OnSwipeMenuClickListener<T>> mListener = new ArrayList<>();

    private ArrayList<Integer> mItemChildIds = new ArrayList<>();
    private ArrayList<OnItemChildClickListener<T>> mItemChildListeners = new ArrayList<>();

    public BaseSingleAdapter(Context context, boolean isOpenLoadMore) {
        super(context, isOpenLoadMore);
    }

    protected abstract int getItemLayoutId();

    protected abstract void convert(ViewHolder holder, T data, int position);

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (isCommonItemView(viewType)) {
            return ViewHolder.create(mContext, getItemLayoutId(), parent);
        }
        return super.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = holder.getItemViewType();
        if (isCommonItemView(viewType)) {
            bindCommonItem(holder, position);
        }
    }

    private void bindCommonItem(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        convert(viewHolder, mDatas.get(position), position);

        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(viewHolder, mDatas.get(position), position);
                }
            }
        });

        for (int i = 0; i < mItemChildIds.size(); i++) {
            final int tempI = i;
            if (viewHolder.getConvertView().findViewById(mItemChildIds.get(i)) != null) {
                viewHolder.getConvertView().findViewById(mItemChildIds.get(i)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemChildListeners.get(tempI).onItemChildClick(viewHolder, mDatas.get(position), position);
                    }
                });
            }
        }

        if (mViewId.size() > 0 && mListener.size() > 0 && viewHolder.getSwipeView() != null) {
            ViewGroup swipeView = (ViewGroup) viewHolder.getSwipeView();

            for (int i = 0; i < mViewId.size(); i++) {
                final int tempI = i;
                swipeView.findViewById(mViewId.get(i)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.get(tempI).onSwipMenuClick(viewHolder, mDatas.get(position), position);
                    }
                });
            }
        }
    }

    @Override
    protected int getViewType(int position, T data) {
        return TYPE_COMMON_VIEW;
    }

    public void setOnItemClickListener(OnItemClickListener<T> itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setOnSwipMenuClickListener(int viewId, OnSwipeMenuClickListener<T> swipeMenuClickListener) {
        mViewId.add(viewId);
        mListener.add(swipeMenuClickListener);
    }

    public void setOnItemChildClickListener(int viewId, OnItemChildClickListener<T> itemChildClickListener) {
        mItemChildIds.add(viewId);
        mItemChildListeners.add(itemChildClickListener);
    }

}

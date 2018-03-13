package com.atgc.hd.base.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.atgc.hd.base.adapter.holder.ViewHolder;
import com.atgc.hd.base.adapter.interfaces.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Othershe
 * Time: 2016/8/29 09:46
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_COMMON_VIEW = 100001;//普通类型 Item
    public static final int TYPE_FOOTER_VIEW = 100002;//footer类型 Item
    public static final int TYPE_EMPTY_VIEW = 100003;//empty view，即初始化加载时的提示View
    public static final int TYPE_NODATE_VIEW = 100004;//初次加载无数据的默认空白view
    public static final int TYPE_RELOAD_VIEW = 100005;//初次加载无数据的可重新加载或提示用户的view

    private OnLoadMoreListener mLoadMoreListener;

    protected Context mContext;
    protected List<T> mDatas;
    //是否开启加载更多
    private boolean mOpenLoadMore;

    //是否可以加载更多
    private boolean loadMoreEnable;

    //是否自动加载，当数据不满一屏幕会自动加载
    private boolean isAutoLoadMore = false;

    private boolean isRemoveEmptyView;

    //分页加载中view
    private View mLoadingView;

    //分页加载失败view
    private View mLoadFailedView;

    //分页加载结束view
    private View mLoadEndView;

    //首次预加载view
    private View mEmptyView;

    //首次预加载失败、或无数据的view
    private View mReloadView;

    //footer view的父布局
    private RelativeLayout mFooterLayout;

    //开始重新加载数据
    private boolean isReset;

    protected abstract int getViewType(int position, T data);

    public BaseAdapter(Context context, boolean isOpenLoadMore) {
        mContext = context;
        mDatas = new ArrayList<T>();
        mOpenLoadMore = isOpenLoadMore;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = null;
        switch (viewType) {
            case TYPE_FOOTER_VIEW:
                if (mFooterLayout == null) {
                    mFooterLayout = new RelativeLayout(mContext);
                }
                viewHolder = ViewHolder.create(mFooterLayout);
                break;
            case TYPE_EMPTY_VIEW:
                viewHolder = ViewHolder.create(mEmptyView);
                break;
            case TYPE_NODATE_VIEW:
                viewHolder = ViewHolder.create(new View(mContext));
                break;
            case TYPE_RELOAD_VIEW:
                viewHolder = ViewHolder.create(mReloadView);
                break;
        }
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        if (mDatas.isEmpty() && mEmptyView != null) {
            return 1;
        } else {
            return mDatas.size() + getFooterViewCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mDatas.isEmpty()) {
            if (mEmptyView != null && !isRemoveEmptyView) {
                return TYPE_EMPTY_VIEW;
            } else if (isRemoveEmptyView && mReloadView != null) {
                return TYPE_RELOAD_VIEW;
            } else {
                return TYPE_NODATE_VIEW;
            }
        }

        if (isFooterView(position)) {
            return TYPE_FOOTER_VIEW;
        }

        return getViewType(position, mDatas.get(position));
    }

    /**
     * 根据positiond得到data
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        if (mDatas.isEmpty()) {
            return null;
        }
        return mDatas.get(position);
    }

    /**
     * 是否是FooterView
     *
     * @param position
     * @return
     */
    private boolean isFooterView(int position) {
        return mOpenLoadMore && position >= getItemCount() - 1;
    }

    protected boolean isCommonItemView(int viewType) {
        return viewType != TYPE_EMPTY_VIEW && viewType != TYPE_FOOTER_VIEW
                && viewType != TYPE_NODATE_VIEW && viewType != TYPE_RELOAD_VIEW;
    }

    /**
     * StaggeredGridLayoutManager模式时，FooterView可占据一行
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if (isFooterView(holder.getLayoutPosition())) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();

            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                p.setFullSpan(true);
            }
        }
    }

    /**
     * GridLayoutManager模式时， FooterView可占据一行，判断RecyclerView是否到达底部
     *
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = ((GridLayoutManager) layoutManager);
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isFooterView(position)) {
                        return gridManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
        startLoadMore(recyclerView, layoutManager);
    }


    /**
     * 判断列表是否滑动到底部
     *
     * @param recyclerView
     * @param layoutManager
     */
    private void startLoadMore(RecyclerView recyclerView, final RecyclerView.LayoutManager layoutManager) {
        if (!mOpenLoadMore) {
            return;
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!isAutoLoadMore && findLastVisibleItemPosition(layoutManager) + 1 == getItemCount()) {
                        scrollLoadMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isAutoLoadMore && findLastVisibleItemPosition(layoutManager) + 1 == getItemCount()) {
                    scrollLoadMore();
                } else if (isAutoLoadMore) {
                    isAutoLoadMore = false;
                }
            }
        });
    }

    /**
     * 到达底部开始刷新
     */
    private void scrollLoadMore() {
        if (!loadMoreEnable) {
            return;
        }
        if (isReset) {
            return;
        }
        if (mFooterLayout.getChildAt(0) == mLoadingView) {
            if (mLoadMoreListener != null) {
                mLoadMoreListener.onLoadMore(false);
            }
        }
    }

    private int findLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof LinearLayoutManager) {
            return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] lastVisibleItemPositions = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            return Util.findMax(lastVisibleItemPositions);
        }
        return -1;
    }

    private void setFooterViewVisiblity(int visiblity) {
        mFooterLayout.setVisibility(visiblity);
    }

    /**
     * 清空footer view
     */
    private void removeFooterView() {
        mFooterLayout.removeAllViews();
    }

    /**
     * 添加新的footer view
     *
     * @param footerView
     */
    private void addFooterView(View footerView) {
        if (footerView == null) {
            return;
        }

        if (mFooterLayout == null) {
            mFooterLayout = new RelativeLayout(mContext);
        }

        removeFooterView();

        RelativeLayout.LayoutParams params
                = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mFooterLayout.addView(footerView, params);
    }

    /**
     * 刷新加载更多的数据
     *
     * @param datas
     */
    public void setLoadMoreData(List<T> datas) {
        int size = mDatas.size();
        mDatas.addAll(datas);
        notifyItemInserted(size);
    }

    /**
     * 下拉刷新，得到的新数据插到原数据起始
     *
     * @param datas
     */
    public void setData(List<T> datas) {
        mDatas.addAll(0, datas);
        notifyDataSetChanged();
    }

    /**
     * 初次加载、或下拉刷新要替换全部旧数据时刷新数据
     *
     * @param datas
     */
    public void setNewData(List<T> datas) {
        if (isReset) {
            isReset = false;
        }

        mDatas.clear();

        if (datas == null || datas.isEmpty()) {
        } else {
            mDatas.addAll(datas);
        }
        notifyDataSetChanged();
    }

    public void addWithAnimation(int startIndex, T entity) {
        getAllData().add(startIndex, entity);
        notifyItemInserted(startIndex);

        if (startIndex < getAllData().size() - 1) {
            notifyItemRangeChanged(startIndex, getAllData().size() - startIndex);
        }
    }

    public void addWithAnimation(int startIndex, List<T> src) {
        int len = src.size();
        for (int i = 0; i < len; i++) {
            T entity = src.get(len - i - 1);
            getAllData().add(startIndex, entity);
            notifyItemInserted(startIndex + i);
        }
        if (startIndex < getAllData().size() - 1) {
            notifyItemRangeChanged(startIndex, getAllData().size() - startIndex);
        }
    }

    public void remove(int position) {
        mDatas.remove(position);
        notifyDataSetChanged();
    }

    public void removeWithAnimation(int startIndex, int count) {
        for (int i = 0; i < count; i++) {
            getAllData().remove(startIndex);
            notifyItemRemoved(startIndex);
        }

        if (startIndex < getAllData().size() - 1) {
            notifyItemRangeChanged(startIndex, getAllData().size() - startIndex);
        }

    }

    public List<T> getAllData() {
        return mDatas;
    }

    /**
     * 初始化加载中布局
     *
     * @param loadingView
     */
    public void setLoadingView(View loadingView) {
        mLoadingView = loadingView;
        addFooterView(mLoadingView);
    }

    public void setLoadingView(int loadingId) {
        setLoadingView(Util.inflate(mContext, loadingId));
    }

    /**
     * 初始加载失败布局
     *
     * @param loadFailedView
     */
    public void setLoadFailedView(View loadFailedView) {
        mLoadFailedView = loadFailedView;
    }

    public void setLoadFailedView(int loadFailedId) {
        setLoadFailedView(Util.inflate(mContext, loadFailedId));
    }

    /**
     * 初始化全部加载完成布局
     *
     * @param loadEndView
     */
    public void setLoadEndView(View loadEndView) {
        mLoadEndView = loadEndView;
    }

    public void setLoadEndView(int loadEndId) {
        setLoadEndView(Util.inflate(mContext, loadEndId));
    }

    /**
     * 初始化emptyView
     *
     * @param emptyView
     */
    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;
    }

    /**
     * 移除emptyView
     */
    public void removeEmptyView() {
        isRemoveEmptyView = true;
        notifyDataSetChanged();
    }

    /**
     * 初次预加载失败、或无数据可显示该view，进行重新加载或提示用户无数据
     *
     * @param reloadView
     */
    public void setReloadView(View reloadView) {
        mReloadView = reloadView;
        isRemoveEmptyView = true;
        notifyDataSetChanged();
    }

    /**
     * 返回 footer view数量
     *
     * @return
     */
    public int getFooterViewCount() {
        return mOpenLoadMore && !mDatas.isEmpty() ? 1 : 0;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        mLoadMoreListener = loadMoreListener;
    }

    /**
     * 重置adapter，恢复到初始状态
     */
    public void reset() {
        if (mLoadingView != null) {
            addFooterView(mLoadingView);
        }
        isReset = true;
        isAutoLoadMore = true;
        mDatas.clear();
    }

    /**
     * 数据加载完成
     */
    public void loadEnd() {
        if (mLoadEndView != null) {
            addFooterView(mLoadEndView);
        }
    }

    /**
     * 数据加载失败
     */
    public void loadFailed() {
        addFooterView(mLoadFailedView);
        mLoadFailedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFooterView(mLoadingView);
                if (mLoadMoreListener != null) {
                    mLoadMoreListener.onLoadMore(true);
                }
            }
        });
    }

    public void setAutoLoadMore(boolean autoLoadMore) {
        isAutoLoadMore = autoLoadMore;
    }

    public void setLoadMoreEnable(boolean loadMoreEnable) {
        this.loadMoreEnable = loadMoreEnable;
        if (loadMoreEnable) {
            setFooterViewVisiblity(View.VISIBLE);
        } else {
            setFooterViewVisiblity(View.GONE);
        }
    }
}

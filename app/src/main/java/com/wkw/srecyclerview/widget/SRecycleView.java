package com.wkw.srecyclerview.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wkw.srecyclerview.R;
import com.wkw.srecyclerview.common.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wukewei on 16/5/18.
 */
public class SRecycleView<T> extends LinearLayout {

    private static final int MAX_PAGE_SIZE = 20;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private SRecyAdapter<T> mAdapter;
    private boolean isRefreshable;
    private OnLoadMoreListener mOnLoadMoreListener;
    private OnRefreshListener mOnRefreshListener;

    private LinearLayoutManager layoutManagerType;


    public SRecycleView(Context context) {
        super(context);
        init(context);
    }

    public SRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        View view = LayoutInflater.from(context).inflate(R.layout.layout_refresh_recycler_view, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        addView(view);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setEnabled(isRefreshable);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mOnRefreshListener != null && isRefreshable) {
                    mOnRefreshListener.onRefresh();
                }
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        layoutManagerType = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManagerType);
        mAdapter = new SRecyAdapter<>();
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            protected int lastVisibleItem;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = layoutManagerType.findLastVisibleItemPosition();

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mRecyclerView.getAdapter() != null && newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == mRecyclerView.getAdapter().getItemCount() && mOnLoadMoreListener != null
                        && !SRecycleView.this.mAdapter.isLoading) {
                       SRecycleView.this.mAdapter.isLoading = true;
                       mOnLoadMoreListener.onLoadMore();
                }
            }
        });

    }


    public class SRecyAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public boolean hasMore;
        protected List<T> mList;
        protected boolean hasHeader = false, isLoading;
        private static final int TYPE_FOOTER = Integer.MIN_VALUE;
        private static final int TYPE_HEADER = Integer.MAX_VALUE;
        private static final int TYPE_ITEM = 0;
        public Object mHeaderData = null;
        public Class<? extends BaseViewHolder> mItemViewClass, mHeaderViewClass;
        public int mItemLayoutId, mHeaderLayoutId;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            try {

                if (viewType == TYPE_FOOTER) {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.layout_recycler_footer, parent, false);
                    return new FooterViewHolder(view);
                } else {
                    if (hasHeader && viewType == TYPE_HEADER) {
                        return mHeaderViewClass.getConstructor(View.class).newInstance(
                                LayoutInflater.from(mContext).inflate(mHeaderLayoutId, parent, false));
                    } else {
                        return mItemViewClass.getConstructor(View.class).newInstance(
                                LayoutInflater.from(mContext).inflate(mItemLayoutId, parent, false));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position + 1 == getItemCount()) {
                if (hasMore) {
                    ((FooterViewHolder) holder).mProgressBar.setVisibility(View.VISIBLE);
                    ((FooterViewHolder) holder).tv_content.setText(R.string.loading);
                } else {
                    ((FooterViewHolder) holder).mProgressBar.setVisibility(View.GONE);
                    ((FooterViewHolder) holder).tv_content.setText(R.string.no_data);
                }
            } else  {
                if (position == 0 && hasHeader) {
                    ((BaseViewHolder)holder).onBindViewHolder(holder.itemView, mHeaderData, position);
                } else {
                    ((BaseViewHolder)holder).onBindViewHolder(holder.itemView, mList.get(position - (hasHeader ? 1 : 0)), position);
                }

//                ((BaseViewHolder)holder).onBindViewHolder(holder.itemView,
//                        (hasHeader && position == 0) ? mHeaderData : mList.get(position - (hasHeader ? 1 : 0)), position);

            }
        }

        @Override
        public int getItemCount() {
            return mList.size() + 1 + (hasHeader ? 1 : 0);
        }

        @Override
        public int getItemViewType(int position) {
            return hasHeader ? (position == 0 ? TYPE_HEADER : (position + 1 == getItemCount() ? TYPE_FOOTER : TYPE_ITEM  )) :
                    ( position + 1 == getItemCount() ? TYPE_FOOTER : TYPE_ITEM ) ;
        }


            public void setItemView(int itemId, Class<? extends BaseViewHolder> itemClass) {
            this.mItemLayoutId = itemId;
            this.hasMore = true;
            this.mList = new ArrayList<>();
            this.mItemViewClass = itemClass;

        }

        public void setHeaderView(int headerId, Class<? extends BaseViewHolder> headerClass, Object o) {
            if (headerClass == null) {
                this.hasHeader = false;
            } else {
                this.hasHeader = true;
                this.mHeaderLayoutId = headerId;
                this.mHeaderViewClass = headerClass;
                this.mHeaderData = o;
            }
        }

        public void setData(List<T> data, boolean isRefreshData) {
            if (data == null) return;
            this.hasMore = data.size() == MAX_PAGE_SIZE;
            this.isLoading = false;
            if (mList == null) mList = new ArrayList<>();
            if (isRefreshData) {
                mList.clear();
                this.hasMore = true;
                this.mList.addAll(data);
            } else {
                this.mList.addAll(data);
            }
            notifyDataSetChanged();
        }


    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {

        private final ProgressBar mProgressBar;
        private final TextView tv_content;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.progressbar);
            tv_content = (TextView) itemView.findViewById(R.id.tv_footer);
        }
    }


    public SRecycleView setItemViewHolder(Class< ? extends BaseViewHolder> cla) {
        try {
            BaseViewHolder mItemHolder = cla.getConstructor(View.class)
                    .newInstance(new View(mContext));
            int mLayoutId = mItemHolder.getLayoutId();
            this.mAdapter.setItemView(mLayoutId, cla);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public SRecycleView setHeaderViewHolder(Class< ? extends BaseViewHolder> cla, Object obj) {
        try {
            BaseViewHolder mHeaderHolder = cla.getConstructor(View.class)
                    .newInstance(new View(mContext));
            int mLayoutId = mHeaderHolder.getLayoutId();
            this.mAdapter.setHeaderView(mLayoutId, cla, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public SRecycleView refreshData(List<T> data) {
        if (this.mSwipeRefreshLayout.isRefreshing()) {
            this.mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //让子弹飞一会
                    SRecycleView.this.mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 800);
        }
        this.mAdapter.setData(data, true);
        return this;
    }

    public SRecycleView addLoadMoreData(List<T> data) {
        this.mAdapter.setData(data, false);
        return this;
    }

    public SRecycleView setHeaderData(Object obj) {
        this.mAdapter.mHeaderData = obj;
        this.mAdapter.notifyItemChanged(0);
        return this;
    }

    public SRecycleView setRefreshable(boolean refreshable) {
        isRefreshable = refreshable;
        this.mSwipeRefreshLayout.setEnabled(refreshable);
        return this;
    }

    public SRecycleView setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.mOnLoadMoreListener = onLoadMoreListener;
        return this;
    }

    public SRecycleView setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener = onRefreshListener;
        return this;
    }


    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public interface OnRefreshListener {
        void onRefresh();
    }
}

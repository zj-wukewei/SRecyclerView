# SRecyclerView
  这是一个加强版本的RecyclerView，支持下拉刷新和上拉加载更多。
     直接引入，
     <com.wkw.srecyclerview.widget.SRecycleView
        android:id="@+id/srecycler_view"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content">
    </com.wkw.srecyclerview.widget.SRecycleView>
    
    
    提供一些方法：
     1: setItemViewHolder（）设置item的布局holder，
     2: setHeaderViewHolder（） 设置头部holder，
     3: addLoadMoreData（） 和 refreshData（）是和数据相关的。
     
    以下是我模拟加载数据的代码：
    
     private void init() {
        for (int i = 0 ; i < 20 ; i ++) {
            data.add("模拟第"+ i +"项数据");
        }

        recycleView.setRefreshable(true)
                .setItemViewHolder(ItemViewHolder.class)
                .setOnRefreshListener(new SRecycleView.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        data.clear();
                        for (int i = 0 ; i < 20 ; i ++) {
                            data.add("模拟第"+ i +"项数据");
                        }
                        getData(true);
                    }
                })
                .setOnLoadMoreListener(new SRecycleView.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        getData(false);

                    }
                });

        getData(true);

    }

    public void getData(final boolean is) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (index == 3) {
                    data.clear();
                    data.add("加载没有了");
                }

                if (is) {
                    recycleView.refreshData(data).setHeaderViewHolder(HeaderViewHolder.class, headerData);
                } else {
                    recycleView.addLoadMoreData(data);
                }

                index ++;

            }
        }, 2000);
    }

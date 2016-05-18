package com.wkw.srecyclerview;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wkw.srecyclerview.viewholder.HeaderViewHolder;
import com.wkw.srecyclerview.viewholder.ItemViewHolder;
import com.wkw.srecyclerview.widget.SRecycleView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SRecycleView recycleView;
    private List<String> data = new ArrayList<>();
    private String headerData = new String("hello header");
    int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recycleView = (SRecycleView) findViewById(R.id.srecycler_view);
        init();
    }

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
}

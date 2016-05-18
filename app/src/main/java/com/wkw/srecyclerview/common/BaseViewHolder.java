package com.wkw.srecyclerview.common;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by wukewei on 16/5/18.
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    protected Context mContext;

    public BaseViewHolder(View itemView) {
        super(itemView);
        this.mContext = itemView.getContext();
    }

    public abstract int getLayoutId();

    public abstract void onBindViewHolder(View itemView, T data, int position);


}

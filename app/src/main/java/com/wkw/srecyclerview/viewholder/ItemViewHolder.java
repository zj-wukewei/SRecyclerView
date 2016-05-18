package com.wkw.srecyclerview.viewholder;

import android.view.View;
import android.widget.TextView;

import com.wkw.srecyclerview.R;
import com.wkw.srecyclerview.common.BaseViewHolder;

/**
 * Created by wukewei on 16/5/18.
 */
public class ItemViewHolder extends BaseViewHolder<String> {

    TextView tv_content;
    public ItemViewHolder(View itemView) {
        super(itemView);
        tv_content = (TextView) itemView.findViewById(R.id.tv_item_content);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_item;
    }

    @Override
    public void onBindViewHolder(View itemView, String data, int position) {

        tv_content.setText(data + position);

    }
}

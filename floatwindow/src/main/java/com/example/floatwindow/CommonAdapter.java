package com.example.floatwindow;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import java.util.List;


public class CommonAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public CommonAdapter(List data) {
        super(R.layout.item_card2, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
//        helper.setText(R.id.tv,item);
        }
}
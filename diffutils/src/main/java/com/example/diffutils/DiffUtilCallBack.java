package com.example.diffutils;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class DiffUtilCallBack extends DiffUtil.Callback {
    private List<PersonInfo> newlist;
    private List<PersonInfo> oldlist;

    public DiffUtilCallBack(List<PersonInfo> newlist, List<PersonInfo> oldlist) {
        this.newlist = newlist;
        this.oldlist = oldlist;
    }

    @Override
    public int getOldListSize() {
        return oldlist.size();
    }

    @Override
    public int getNewListSize() {
        return newlist.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        //判断是否是同一个item，可以在这里处理   判断是否是相同item的逻辑，比如id之类的
        return newlist.get(newItemPosition).getIndex() == oldlist.get(oldItemPosition).getIndex();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        //判断数据是否发生改变，这个  方法会在上面的方法返回true时调用，  因为虽然item是同一个，但有可能item的数据发生了改变
        return newlist.get(newItemPosition).getName().equals(oldlist.get(oldItemPosition).getName());
    }
}

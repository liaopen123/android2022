package com.example.diffutils;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.DiffVH> {
    private List<PersonInfo> mDatas;
    private LayoutInflater mInflater;

    public PersonAdapter(Context context, List<PersonInfo> mDatas) {
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(context);
    }

    public void setDatas(List<PersonInfo> mDatas) {
        this.mDatas = mDatas;
    }

    @Override
    public DiffVH onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DiffVH(mInflater.inflate(R.layout.item_person, parent, false));
    }

    @Override
    public void onBindViewHolder(final DiffVH holder, final int position) {
        PersonInfo personInfo = mDatas.get(position);
        holder.tv_index.setText(String.valueOf(personInfo.getIndex()));
        holder.tv_name.setText(String.valueOf(personInfo.getName()));
    }

    @Override
    public void onBindViewHolder(DiffVH holder, int position, List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            Bundle payload = (Bundle) payloads.get(0);
            PersonInfo bean = mDatas.get(position);
            for (String key : payload.keySet()) {
                switch (key) {
                    case "KEY_INDEX":
                        holder.tv_index.setText(String.valueOf(bean.getIndex()));
                        break;
                    case "KEY_NAME":
                        holder.tv_name.setText(String.valueOf(bean.getName()));
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    class DiffVH extends RecyclerView.ViewHolder {
        TextView tv_index;
        TextView tv_name;

        public DiffVH(View view) {
            super(view);
            tv_index = view.findViewById(R.id.tv_index);
            tv_name = view.findViewById(R.id.tv_name);
        }
    }
}


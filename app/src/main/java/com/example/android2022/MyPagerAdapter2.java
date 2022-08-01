package com.example.android2022;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyPagerAdapter2 extends FragmentStatePagerAdapter {

    private Context mContext;
    private List<Fragment> myPagerFragmentList;

    public MyPagerAdapter2(Context context , ArrayList<Fragment> list, @NonNull FragmentManager fm) {
        super(fm);
        mContext = context;
        myPagerFragmentList = list;
    }


    @Override
    public int getCount() {
        return myPagerFragmentList.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return myPagerFragmentList.get(position);
    }


}
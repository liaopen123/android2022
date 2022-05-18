package com.hhz.test;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomePageAdapter extends FragmentStatePagerAdapter {//FragmentPagerAdapter

    //    private FragmentManager fm;
    private ArrayList<Fragment> fragments = null;
    private List<String> hotIssuesList;
    private Context context;

    public HomePageAdapter(Context context, FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
        notifyDataSetChanged();
    }

    public HomePageAdapter(Context context, FragmentManager fm, List<String> hotIssuesList) {
        super(fm);
        this.context = context;
        this.hotIssuesList = hotIssuesList;
        notifyDataSetChanged();
//        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int arg0) {
//        Fragment fragment = new ColourFragment();
//        Bundle args = new Bundle();
//        args.putInt("title", arg0);
//        args.putSerializable("content",hotIssuesList.get(arg0));
//        fragment.setArguments(args);
//        return fragment;
        return fragments.get(arg0);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return fragments.size();//hotIssuesList.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
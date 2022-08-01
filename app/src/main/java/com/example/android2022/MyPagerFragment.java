package com.example.android2022;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyPagerFragment extends androidx.fragment.app.Fragment {

    private final String s;

    public MyPagerFragment(@NotNull String s) {
        this.s = s;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_outer, container, false);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewPager viewPager = view.findViewById(R.id.innerViewPager);
     ArrayList<Fragment> fragments=new ArrayList<Fragment>();
        fragments.add(new MyInnerFragment("1"));
        fragments.add(new MyInnerFragment("2"));
        fragments.add(new MyInnerFragment("3"));
        viewPager.setAdapter(new MyPagerAdapter2(requireActivity(),fragments,getChildFragmentManager()));
    }
}

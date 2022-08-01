package com.example.android2022;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

public class MyInnerFragment extends androidx.fragment.app.Fragment {

    private final String s;

    public MyInnerFragment(@NotNull String s) {
        this.s = s;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = LayoutInflater.from(requireActivity()).inflate(R.layout.fragment_inner, container, false);
        return inflate;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView viewById = view.findViewById(R.id.tvTitle);
        viewById.setText("s = " + System.currentTimeMillis());
    }
}

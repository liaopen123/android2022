package com.hhz.test

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

class Main2Activity : AppCompatActivity() {
    val list :ArrayList<Fragment> = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maint)
       val vp =  findViewById<ViewPager>(R.id.vp)
        val recyclerview = findViewById(R.id.recyclerview);

        val ms =  LinearLayoutManager(this);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL)

        recyclerview.setLayoutManager(ms);

        val snapHelper =  PagerSnapHelper();snapHelper.attachToRecyclerView(recyclerview);//初始化数据

        val datas :List<String> =  ArrayList<String>();

        for (int i = 0; i < 10; i++) {

            datas.add("item " + i);

        }

//设置Adapter

        recyclerview.setAdapter(new GeneralAdapter(this, datas));

    }



}
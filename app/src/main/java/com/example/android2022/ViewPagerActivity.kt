package com.example.android2022

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager

class ViewPagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_viewpager)
        val outterViewPager = findViewById<ViewPager>(R.id.viewPager)
       val fragments: ArrayList<Fragment> = ArrayList<Fragment>()
        fragments.add(MyPagerFragment1("1"))
        fragments.add(MyPagerFragment("2"))
        fragments.add(MyPagerFragment1("3"))
        outterViewPager.adapter = MyPagerAdapter2(this,fragments,supportFragmentManager)
    }
}
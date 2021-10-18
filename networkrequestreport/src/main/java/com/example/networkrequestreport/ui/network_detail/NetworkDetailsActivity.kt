package com.example.networkrequestreport.ui.network_detail

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.example.networkrequestreport.R
import com.example.networkrequestreport.report.NetworkReportQueue

class NetworkDetailsActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val position = intent.getIntExtra("position",0)
        setContentView(R.layout.activity_network_details)
       val viewPager =  findViewById<ViewPager>(R.id.view_pager)
       val tabs =  findViewById<TabLayout>(R.id.net_tabs)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager,NetworkReportQueue.getQueue()[position])
        viewPager.adapter = sectionsPagerAdapter
        tabs.setupWithViewPager(viewPager)
    }
}
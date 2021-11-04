package com.example.coordinate_scrool

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val titles = arrayOf("1","2")
        val vp = findViewById<ViewPager>(R.id.vp_collect)
        val tab = findViewById<TabLayout>(R.id.tab_collect)
        vp.adapter = object: FragmentStatePagerAdapter(supportFragmentManager) {
            override fun getPageTitle(position: Int): CharSequence? {
                return titles[position]
            }

            override fun getCount()= titles.size

            override fun getItem(position: Int): Fragment {
                return  if (position==0){
                    WebViewFragment.newInstance("","")
                }else{
                    BlankFragment.newInstance("","")
                }
            }

        };

        tab.setupWithViewPager(vp)
    }
}
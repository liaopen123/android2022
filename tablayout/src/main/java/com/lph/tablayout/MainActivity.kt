package com.lph.tablayout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.ViewPager
import com.lph.tablayout.databinding.ActivityMainBinding
import com.lph.tablayout.ui.main.SectionsPagerAdapter
import com.lph.tablayout.ui.main.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        val viewPager: ViewPager = binding.viewPager
        viewPager.adapter = sectionsPagerAdapter

        val tabs: TabLayout = binding.tabs
//        tabs.addTab(tabs.newTab().setIcon(R.mipmap.ic_launcher))
//        tabs.addTab(tabs.newTab().setText("lph"))
//        tabs.addTab(tabs.newTab().setText("lph22"))
//        tabs.addTab(tabs.newTab().setText("lph33"))
//        tabs.addTab(tabs.newTab().setText("lph"))
//        tabs.addTab(tabs.newTab().setText("lph"))
//        tabs.addTab(tabs.newTab().setText("lph"))
//        tabs.addTab(tabs.newTab().setText("lph"))
//        tabs.addTab(tabs.newTab().setText("lph"))
//        tabs.addTab(tabs.newTab().setText("lph"))
//        tabs.addTab(tabs.newTab().setText("lph"))
//        tabs.addTab(tabs.newTab().setText("lph"))
//        tabs.tabMode = TabLayout.MODE_SCROLLABLE
        tabs.setupWithViewPager(viewPager)
        initTabInfo()
//        viewPager.currentItem = 2
    }

    private fun initTabInfo() {
        val titles = arrayOf<String>("精选","最新","images")
        val tabs: TabLayout = binding.tabs
        titles.forEachIndexed { index, s ->
            tabs.getTabAt(index)!!.customView = getTabView(index,s)
        }

        tabs.addOnTabSelectedListener(object :TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                changeTabSelect(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                changeTabNormal(tab)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

    }

    private fun changeTabNormal(tab: TabLayout.Tab?) {
        tab?.customView?.apply {
            val tvSelected = this.findViewById<TextView>(R.id.tv_title_selected)
            val tvUnselected = this.findViewById<TextView>(R.id.tv_title_unselected)
            val ivSelected = this.findViewById<ImageView>(R.id.iv_selected)
            val ivUnselected = this.findViewById<ImageView>(R.id.iv_unselected)
            if (tvSelected.visibility == View.VISIBLE||tvUnselected.visibility == View.VISIBLE) {
                //说明是文字
                tvSelected.visibility = View.INVISIBLE
                tvUnselected.visibility = View.VISIBLE
            }else{
                //说明是图片
                ivSelected.visibility = View.INVISIBLE
                ivUnselected.visibility = View.VISIBLE
            }
        }
    }

    private fun changeTabSelect(tab: TabLayout.Tab?) {
        tab?.customView?.apply {
            val tvSelected = this.findViewById<TextView>(R.id.tv_title_selected)
            val tvUnselected = this.findViewById<TextView>(R.id.tv_title_unselected)
            val ivSelected = this.findViewById<ImageView>(R.id.iv_selected)
            val ivUnselected = this.findViewById<ImageView>(R.id.iv_unselected)
            if (tvSelected.visibility == View.VISIBLE||tvUnselected.visibility == View.VISIBLE) {
                //说明是文字
                tvSelected.visibility = View.VISIBLE
                tvUnselected.visibility = View.INVISIBLE
            }else{
                //说明是图片
                ivSelected.visibility = View.VISIBLE
                ivUnselected.visibility = View.INVISIBLE
            }
        }
    }

    private fun getTabView(index: Int, title: String): View {
        val view: View = LayoutInflater.from(this).inflate(R.layout.item_rank_tab, null)
        val clContainer = view.findViewById<ConstraintLayout>(R.id.cl_container)
        val tvSelected = view.findViewById<TextView>(R.id.tv_title_selected)
        val tvUnselected = view.findViewById<TextView>(R.id.tv_title_unselected)
        val ivSelected = view.findViewById<ImageView>(R.id.iv_selected)
        val ivUnselected = view.findViewById<ImageView>(R.id.iv_unselected)
        if (title.equals("images")) {
            tvSelected.visibility = View.INVISIBLE
            tvUnselected.visibility = View.INVISIBLE
            ivSelected.setImageResource(R.mipmap.icon_seleted)
            ivUnselected.setImageResource(R.mipmap.icon_unselected)
            if (index==0) {
                ivSelected.visibility = View.VISIBLE
            }
        }else{
            ivSelected.visibility = View.INVISIBLE
            ivUnselected.visibility = View.INVISIBLE
            tvSelected.text = title
            tvUnselected.text = title
            if (index==0) {
                tvSelected.visibility = View.VISIBLE
            }
        }

        if (index==0) {
            clContainer.setPadding(resources.getDimension(R.dimen.tab_padding_first_left).toInt(),0,resources.getDimension(R.dimen.tab_padding_first_right).toInt(),0)
        }
        return  view
    }



}
package com.lph.tablayout

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lph.tablayout.ui.main.PlaceholderFragment


class ViewPager2Activity : AppCompatActivity() {
        private var tabLayout: TabLayout? = null
        private var viewPager2: ViewPager2? = null
        private val activeColor: Int = Color.parseColor("#ff678f")
        private val normalColor: Int = Color.parseColor("#666666")
        private val activeSize = 20
        private val normalSize = 14
        private val fragments: ArrayList<Fragment>? = null
        private var mediator: TabLayoutMediator? = null
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_view_pager2)
            tabLayout = findViewById(R.id.tab_layout)
            viewPager2 = findViewById(R.id.view_pager)
            val tabs = arrayOf("关注", "推荐", "最新")

            //禁用预加载
            viewPager2?.setOffscreenPageLimit(ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT)
            //Adapter
            viewPager2?.setAdapter(object : FragmentStateAdapter(supportFragmentManager, lifecycle) {
                @NonNull
                override fun createFragment(position: Int): Fragment {
                    //FragmentStateAdapter内部自己会管理已实例化的fragment对象。
                    // 所以不需要考虑复用的问题
                    return PlaceholderFragment.newInstance(position)
                }

                override fun getItemCount(): Int {
                    return tabs.size
                }
            })
            //viewPager 页面切换监听
            viewPager2?.registerOnPageChangeCallback(changeCallback)
            mediator =
                TabLayoutMediator(tabLayout!!, viewPager2!!, object : TabLayoutMediator.TabConfigurationStrategy {
                    override fun onConfigureTab(@NonNull tab: TabLayout.Tab, position: Int) {
                        //这里可以自定义TabView
                        val tabView = TextView(this@ViewPager2Activity)
                        val states = arrayOfNulls<IntArray>(2)
                        states[0] = intArrayOf(android.R.attr.state_selected)
                        states[1] = intArrayOf()
                        val colors = intArrayOf(activeColor, normalColor)
                        val colorStateList = ColorStateList(states, colors)
                        tabView.setText(tabs[position])
                        tabView.setTextSize(normalSize.toFloat())
                        tabView.setTextColor(colorStateList)
                        tab.setCustomView(tabView)
                    }
                })
            //要执行这一句才是真正将两者绑定起来
            mediator?.attach()
        }

        private val changeCallback: OnPageChangeCallback = object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                //可以来设置选中时tab的大小
                val tabCount: Int = tabLayout!!.getTabCount()
                for (i in 0 until tabCount) {
                    val tab: TabLayout.Tab = tabLayout!!.getTabAt(i)!!
                    val tabView: TextView = tab.getCustomView() as TextView
                    if (tab.getPosition() === position) {
                        tabView.setTextSize(activeSize.toFloat())
                        tabView.setTypeface(Typeface.DEFAULT_BOLD)
                    } else {
                        tabView.setTextSize(normalSize.toFloat())
                        tabView.setTypeface(Typeface.DEFAULT)
                    }
                }
            }
        }

        override fun onDestroy() {
            mediator?.detach()
            viewPager2!!.unregisterOnPageChangeCallback(changeCallback)
            super.onDestroy()
        }
    }
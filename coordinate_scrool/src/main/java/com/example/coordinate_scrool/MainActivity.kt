package com.example.coordinate_scrool

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.functions.Consumer
import java.lang.Exception


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


        /**
         * Created by 朱大大
         * QQ:941556675
         */
        /**
         * Created by 朱大大
         * QQ:941556675
         */
        val rxPermissions = RxPermissions(this)
        rxPermissions.request( Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .subscribe { aBoolean ->
                if (aBoolean) {
                    //表示用户同意权限
                    Toast.makeText(
                        this@MainActivity,
                        "用户同意使用权限", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    //表示用户不同意权限
                    Toast.makeText(
                        this@MainActivity,
                        "用户拒绝使用权限", Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }






}
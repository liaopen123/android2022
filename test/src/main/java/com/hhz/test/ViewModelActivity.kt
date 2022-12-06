package com.hhz.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import com.hhz.test.viewmodel.HomeViewModel

class ViewModelActivity : AppCompatActivity() {

    private val photoListViewModel by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }
    private val photoListViewModel1 by lazy { ViewModelProvider(this).get(HomeViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_model)

        photoListViewModel.request()

        photoListViewModel.getPersonalHomepageInfoObs.observe(this){
            Log.e("廖鹏辉","$it")
        }

        photoListViewModel1.getPersonalHomepageInfoObs.observe(this){
            Log.e("廖鹏辉","$it")
        }
    }
}
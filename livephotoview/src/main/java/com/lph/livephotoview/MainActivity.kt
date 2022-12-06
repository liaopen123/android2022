package com.lph.livephotoview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val livePhotoView = findViewById<LivePhotoView>(R.id.lp)
        livePhotoView.initData("","")
    }
}
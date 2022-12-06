package com.hhz.test.shimmer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.hhz.test.R

class ShimmerMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shimmer_main)
        val shimmer = findViewById<ShimmerFrameLayout>(R.id.shimmer)
        shimmer.startShimmer()
    }
}
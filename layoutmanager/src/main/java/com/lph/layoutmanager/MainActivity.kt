package com.lph.layoutmanager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val TAG: String = "LPH"
    private var totalDy: Int =0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.rv)
        val rvBottom = findViewById<RecyclerView>(R.id.rv_bottom)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = IndicatorAdapter(this)
        rvBottom.layoutManager = LinearLayoutManager(this)
        rvBottom.adapter = IndicatorAdapter(this)
        recyclerView.addOnScrollListener(object:RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalDy += dy
                Log.e(TAG,"totalDy:${totalDy}")
            }
        })

    }

}
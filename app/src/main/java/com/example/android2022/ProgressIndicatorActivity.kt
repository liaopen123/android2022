package com.example.android2022

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android2022.adapter.IndicatorAdapter

class ProgressIndicatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_progress_indocator)
        var  rv = findViewById<RecyclerView>(R.id.rv)
        rv.apply {
            layoutManager = LinearLayoutManager(this@ProgressIndicatorActivity)
            adapter = IndicatorAdapter(this@ProgressIndicatorActivity)
        }

    }
}
package com.example.floatwindow

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

class MainActivity : AppCompatActivity() {
    private var lastX: Int=0
    private var lastY: Int=0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val content = findViewById<RelativeLayout>(R.id.rlContent)
        val params = content.layoutParams
        findViewById<ScaleImage>(R.id.ivScale).onScaledListener =
            object : ScaleImage.OnScaledListener {
                override fun onScaled(x: Float, y: Float, event: MotionEvent) {
                    params.width = max(params.width + x.toInt(), 200)
                    params.height = max(params.height + y.toInt(), 200)
                    content.layoutParams = params
                }
            }
        val rv = findViewById<RecyclerView>(R.id.rv)
        RvUtils.loadData(rv)
    }
}
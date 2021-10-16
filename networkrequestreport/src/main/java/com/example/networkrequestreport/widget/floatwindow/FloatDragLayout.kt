package com.example.networkrequestreport.widget.floatwindow

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.RelativeLayout


class FloatDragLayout(context: Context, attrs: AttributeSet? = null) :RelativeLayout(context, attrs) {
    private var lastX = 0
    private var lastY = 0

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //获取到手指处的横坐标和纵坐标
        val x = event.x.toInt()  //
        val y = event.y.toInt()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = x
                lastY = y
            }

            MotionEvent.ACTION_MOVE -> {
                //计算移动的距离
                val offX = x - lastX
                val offY = y - lastY
                val marginLayoutParams = layoutParams as ViewGroup.MarginLayoutParams
                marginLayoutParams.leftMargin = left+offX
                marginLayoutParams.topMargin = top+offY
                layoutParams = marginLayoutParams

            }
        }

        return true
    }
}
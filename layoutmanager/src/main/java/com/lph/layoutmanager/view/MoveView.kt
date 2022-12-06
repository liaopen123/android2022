package com.lph.layoutmanager.view

import android.content.Context
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ViewCompat


class MoveView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var lastOffset = Point(0, 0)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastOffset = Point(event.x.toInt(), event.y.toInt())
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - lastOffset.x
                val dy = event.y - lastOffset.y

                ViewCompat.offsetLeftAndRight(this, dx.toInt())
                ViewCompat.offsetTopAndBottom(this, dy.toInt())
            }

//            MotionEvent.ACTION_UP->{
//                ViewCompat.offsetLeftAndRight(this, -100)
//                ViewCompat.offsetTopAndBottom(this, -100)
//            }
        }
        return true
    }
}

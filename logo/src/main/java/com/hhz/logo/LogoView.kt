package com.hhz.logo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.min


class LogoView @JvmOverloads constructor(
    private val mContext: Context,
    attributeSet: AttributeSet? = null,
    style: Int = 0
) : View(mContext, attributeSet, style) {
    var maxWidth = 0
    var paint: Paint = Paint()

    init {
        initPainter()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        maxWidth = min(measuredWidth, measuredHeight)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawText("2222",1f,1f,paint)


    }

    fun initPainter() {
        // 背景画笔
        paint = Paint()
        paint.setColor(Color.WHITE)
        paint.setAntiAlias(true)
        paint.setStyle(Paint.Style.STROKE)
        paint.setStrokeWidth(20f)
    }


}
package com.hhz.test.guide

import android.content.res.Resources
import android.graphics.RectF
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.hubert.guide.NewbieGuide
import com.app.hubert.guide.model.GuidePage
import com.app.hubert.guide.model.HighLight
import com.hhz.test.R
import com.hhz.test.exception.ExceptionUtils
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer

class GuideActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)
        val view = findViewById<View>(R.id.ivPublish)
        view.setOnClickListener {
            try {
                val i = 1/0
            }catch (e:Exception){
                e.printStackTrace()
                val writer: Writer = StringWriter()
                val pw = PrintWriter(writer)
                e.printStackTrace(pw)
                pw.close()
                val error = writer.toString()
                ExceptionUtils.writeException(this@GuideActivity,error,"error")
            }
        }
        val marginHorizontal = 50f
        val marginTop = 10f
        val viewHeight = 44f
        resources.displayMetrics.widthPixels
        val left = dp2px(marginHorizontal).toFloat()
        val top = dp2px(marginTop).toFloat()
        val right =(resources.displayMetrics.widthPixels - dp2px(marginHorizontal)).toFloat()
        val bottom = dp2px(marginTop+viewHeight).toFloat()
        NewbieGuide.with(this)
            .setLabel("guide1")
            .alwaysShow(true)
            .addGuidePage(
                GuidePage.newInstance().addHighLight(
                    RectF(left, top, right, bottom),
                    HighLight.Shape.ROUND_RECTANGLE,
                    dp2px(40f)
                )
                    .setLayoutRes(R.layout.view_version6_guide)
            ).addGuidePage(
                GuidePage.newInstance().addHighLight(view, HighLight.Shape.CIRCLE, 20)
                    .setLayoutRes(R.layout.view_version6_guide2)
            )
            .show()
    }

    fun dp2px(dpValue: Float): Int {
        return (TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            Resources.getSystem().displayMetrics
        ) + 0.5f).toInt()
    }
}
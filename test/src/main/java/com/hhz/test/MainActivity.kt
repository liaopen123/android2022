package com.hhz.test

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.view.ViewTreeObserver
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        val tv = findViewById<TextView>(R.id.tv)
//        toggleEllipsizeOriginal(this,
//            tv, 2,
//            "天不错今天天不错今天天不天不错今不错错今不错今天不错今天今天不错今天天不错今天天不错不错",
//            "展开全部",
//            R.color.black, false)


    }


    /**
     * 设置textView结尾...后面显示的文字和颜色
     * @param context 上下文
     * @param textView textview
     * @param minLines 最少的行数
     * @param originText 原文本
     * @param endText 结尾文字
     * @param endColorID 结尾文字颜色id
     * @param isExpand 当前是否是展开状态
     */
    fun toggleEllipsize(
        context: Context,
        textView: TextView,
        minLines: Int,
        originText: String,
        endText: String,
        endColorID: Int,
        isExpand: Boolean
    ) {
        if (TextUtils.isEmpty(originText)) {
            return
        }
        textView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (isExpand) {
                    textView.text = originText
                } else {
                    val paddingLeft = textView.paddingLeft
                    val paddingRight = textView.paddingRight
                    val paint = textView.paint
                    val moreText = textView.textSize * endText.length
                    val availableTextWidth = (textView.width - paddingLeft - paddingRight) *
                            minLines - moreText
                    val ellipsizeStr = TextUtils.ellipsize(
                        originText, paint,
                        availableTextWidth, TextUtils.TruncateAt.END
                    )
                    if (ellipsizeStr.length < originText.length) {
                        val temp: CharSequence = ellipsizeStr.toString() + endText
                        val ssb = SpannableStringBuilder(temp)
                        ssb.setSpan(
                            ForegroundColorSpan(context.getResources().getColor(endColorID)),
                            temp.length - endText.length, temp.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        textView.text = ssb
                    } else {
                        textView.text = originText
                    }
                }
                textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }



    /**
     * 设置textView结尾...后面显示的文字和颜色
     * @param context 上下文
     * @param textView textview
     * @param minLines 最少的行数
     * @param originText 原文本
     * @param endText 结尾文字
     * @param endColorID 结尾文字颜色id
     * @param isExpand 当前是否是展开状态
     */
    fun toggleEllipsizeOriginal(
        context: Context,
        textView: TextView,
        minLines: Int,
        originText: String,
        endText: String,
        endColorID: Int,
        isExpand: Boolean
    ) {
        if (TextUtils.isEmpty(originText)) {
            return
        }
        textView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (isExpand) {
                    textView.text = originText
                } else {
                    val paddingLeft = textView.paddingLeft
                    val paddingRight = textView.paddingRight
                    val paint = textView.paint
                    val moreText = textView.textSize * endText.length
                    val availableTextWidth = (textView.width - paddingLeft - paddingRight) *
                            minLines - moreText
                    val ellipsizeStr = TextUtils.ellipsize(
                        originText, paint,
                        availableTextWidth, TextUtils.TruncateAt.END
                    )
                    if (ellipsizeStr.length < originText.length) {
                        val temp: CharSequence = ellipsizeStr.toString() + endText
                        val ssb = SpannableStringBuilder(temp)
                        ssb.setSpan(
                            ForegroundColorSpan(context.getResources().getColor(endColorID)),
                            temp.length - endText.length, temp.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        textView.text = ssb
                    } else {
                        toggleEllipsizeExpand(context, textView, minLines, originText, endText, endColorID, isExpand)
                    }
                }
                textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    fun toggleEllipsizeExpand(
        context: Context,
        textView: TextView,
        minLines: Int,
        originText: String,
        endText: String,
        endColorID: Int,
        isExpand: Boolean
    ) {
        if (TextUtils.isEmpty(originText)) {
            return
        }
        val surfix =  " 查看更多"
        val surfix1 = "        "
        val newOriginalText =  originText+surfix;
        val newOriginalText1 =  originText+surfix1;
        textView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (isExpand) {
                    textView.text = newOriginalText
                } else {
                    val paddingLeft = textView.paddingLeft
                    val paddingRight = textView.paddingRight
                    val paint = textView.paint
                    val moreText = textView.textSize * endText.length
                    val availableTextWidth = (textView.width - paddingLeft - paddingRight) *
                            minLines - moreText
                    val ellipsizeStr = TextUtils.ellipsize(
                        newOriginalText, paint,
                        availableTextWidth, TextUtils.TruncateAt.END
                    )
                    val ellipsizeStr1 = TextUtils.ellipsize(
                        newOriginalText1, paint,
                        availableTextWidth, TextUtils.TruncateAt.END
                    )
                    if (ellipsizeStr.length < newOriginalText1.length) {
                        val temp: CharSequence = ellipsizeStr1.toString() + endText
                        val ssb = SpannableStringBuilder(temp)
                        ssb.setSpan(
                            ForegroundColorSpan(context.getResources().getColor(endColorID)),
                            temp.length - endText.length, temp.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                        textView.text = ssb
                    } else {
                        textView.text = newOriginalText
                    }
                }
                textView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

}
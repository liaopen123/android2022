package com.hhz.test

import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.hhz.test.databinding.ViewHomeTopTabBinding

class HomeTopTabBar @JvmOverloads constructor(mContext: Context, attributeSet: AttributeSet? = null, style: Int = 0) :
    FrameLayout(mContext, attributeSet, style) {
    private var textDefaultSize: Float = 0f
    private var textSelectSize: Float = 0f
    private var textSelectColor: Int = 0
    private var textDefaultColor: Int = 0
    private var textSelectedPosition: Int = 1//选中位置
    private var mListener: HomeTopTabBar.TabChangeListener? = null
    private val viewBinding = ViewHomeTopTabBinding.inflate(LayoutInflater.from(mContext), this, true)
    private val tabs = ArrayList<TextView>()

    init {
        attributeSet?.apply {
            val array: TypedArray = mContext.obtainStyledAttributes(this, R.styleable.HomeTopTabBar)
            textDefaultColor = array.getColor(R.styleable.HomeTopTabBar_htb_default_color, -0x666666)
            textSelectColor = array.getColor(R.styleable.HomeTopTabBar_htb_selected_color, -0x000000)
            textDefaultSize = array.getDimension(R.styleable.HomeTopTabBar_htb_default_textSize, 18f)
            textSelectSize = array.getDimension(R.styleable.HomeTopTabBar_htb_selected_textSize, 24f)
            textSelectedPosition = array.getInteger(R.styleable.HomeTopTabBar_htb_default_selected_position, 1)
            array.recycle()
        }
        tabs.apply {
            add(viewBinding.tvFocus)
            add(viewBinding.tvRecommend)
            add(viewBinding.tvCircle)
        }
        initTabState()
        initClickAnimation()
    }

    private fun initClickAnimation() {
        tabs.forEachIndexed { index, tab ->
            tab.setOnClickListener {
                if (index != textSelectedPosition) {
                    startTextAnimation(tabs[textSelectedPosition], tabs[index])
                    mListener?.onTabSelected?.invoke(index)
                    textSelectedPosition = index
                }
            }
        }
    }

    /**
     * oldTv:现在被选中的
     * newTv:要被选中的
     */
    private fun startTextAnimation(oldTv: TextView, newTv: TextView) {
        val animatorSet = AnimatorSet()
        val oldTvSmallAnim = ValueAnimator.ofFloat(textSelectSize, textDefaultSize).apply {
            addUpdateListener { animation ->
                val textSize = animation.animatedValue as Float
                oldTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            }
        }
        val newTvBigAnim = ValueAnimator.ofFloat(textDefaultSize, textSelectSize).apply {
            addUpdateListener { animation ->
                val textSize = animation.animatedValue as Float
                newTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            }
        }
        val oldTvColorAnim = ValueAnimator.ofInt(textSelectColor, textDefaultColor).apply {
            setEvaluator(ArgbEvaluator())
            addUpdateListener { animation ->
                val color = animation.animatedValue as Int
                oldTv.setTextColor(color)
            }
        }

        val newTvColorAnim = ValueAnimator.ofInt(textDefaultColor, textSelectColor).apply {
            setEvaluator(ArgbEvaluator())
            addUpdateListener { animation ->
                val color = animation.animatedValue as Int
                newTv.setTextColor(color)
            }
        }
        animatorSet.playTogether(oldTvSmallAnim, oldTvColorAnim, newTvBigAnim, newTvColorAnim);
        animatorSet.duration = 100
        animatorSet.start()
    }

    private fun initTabState() {
        tabs.forEachIndexed { index, textView ->
            if (index == textSelectedPosition) {
                textView.setTextColor(textSelectColor)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSelectSize)
            } else {
                textView.setTextColor(textDefaultColor)
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textDefaultSize)
            }
        }
    }

    fun addTabChangeListener(listener: TabChangeListener.() -> Unit) {
        mListener = TabChangeListener().also(listener)
    }


    inner class TabChangeListener {
        internal var onTabSelected: ((position: Int) -> Unit)? = null

        fun onTabSelected(action: (position: Int) -> Unit) {
            onTabSelected = action
        }
    }
}
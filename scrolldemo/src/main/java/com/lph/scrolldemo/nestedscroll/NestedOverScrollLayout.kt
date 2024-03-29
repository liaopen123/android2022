package com.lph.scrolldemo.nestedscroll

import android.animation.ValueAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AbsListView
import android.widget.ScrollView
import android.widget.Scroller
import androidx.core.view.*
import androidx.viewpager.widget.ViewPager

open class NestedOverScrollLayout : ViewGroup, NestedScrollingParent3 {

    private var mVelocityTracker = VelocityTracker.obtain()
    private var mScroller = Scroller(context)

    private var mParentHelper: NestedScrollingParentHelper? = null

    private var mTouchSlop: Int = 0
    private var mMinimumVelocity: Float = 0f
    private var mMaximumVelocity: Float = 0f
    private var mCurrentVelocity: Float = 0f

    // 阻尼滑动参数
    private val mMaxDragRate = 2.5f
    private val mMaxDragHeight = 250
    private val mScreenHeightPixels = context.resources.displayMetrics.heightPixels

    private var mHandler: Handler? = null
    private var mNestedInProgress = false
    private var mIsAllowOverScroll = true           // 是否允许过渡滑动
    private var mPreConsumedNeeded = 0              // 在子 View 滑动前，此View需要滑动的距离
    private var mSpinner = 0f                       // 当前竖直方向上 translationY 的距离

    private var mReboundAnimator: ValueAnimator? = null
//    private var mReboundInterpolator = ReboundInterpolator(INTERPOLATOR_VISCOUS_FLUID)

    private var mAnimationRunnable: Runnable? = null    // 用来实现fling时，先过度滑动再回弹的效果
    private var mVerticalPermit = false                 // 控制fling时等待contentView回到translation = 0 的位置

    private var mRefreshContent: View? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setWillNotDraw(false)
        mHandler = Handler(Looper.getMainLooper())
        mParentHelper = NestedScrollingParentHelper(this)
        ViewConfiguration.get(context).let {
            mTouchSlop = it.scaledTouchSlop
            mMinimumVelocity = it.scaledMinimumFlingVelocity.toFloat()
            mMaximumVelocity = it.scaledMaximumFlingVelocity.toFloat()
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        val childCount = super.getChildCount()

        for (i in 0 until childCount) {
            val childView = super.getChildAt(i)
            if (SmartUtil.isContentView(childView)) {
                mRefreshContent = childView
                break
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var minimumWidth = 0
        var minimumHeight = 0
        val thisView = this
        for (i in 0 until super.getChildCount()) {
            val childView = super.getChildAt(i)
            if (childView == null || childView.visibility == GONE) continue

            if (mRefreshContent == childView) {
                mRefreshContent?.let { contentView ->
                    val lp = contentView.layoutParams
                    val mlp = lp as? MarginLayoutParams
                    val leftMargin = mlp?.leftMargin ?: 0
                    val rightMargin = mlp?.rightMargin ?: 0
                    val bottomMargin = mlp?.bottomMargin ?: 0
                    val topMargin = mlp?.topMargin ?: 0
                    val widthSpec = getChildMeasureSpec(
                        widthMeasureSpec,
                        thisView.paddingLeft + thisView.paddingRight + leftMargin + rightMargin, lp.width
                    )
                    val heightSpec = getChildMeasureSpec(
                        heightMeasureSpec,
                        thisView.paddingTop + thisView.paddingBottom + topMargin + bottomMargin, lp.height
                    )
                    contentView.measure(widthSpec, heightSpec)
                    minimumWidth += contentView.measuredWidth
                    minimumHeight += contentView.measuredHeight
                }
            }
        }

        minimumWidth += thisView.paddingLeft + thisView.paddingRight
        minimumHeight += thisView.paddingTop + thisView.paddingBottom
        super.setMeasuredDimension(
            resolveSize(minimumWidth.coerceAtLeast(super.getSuggestedMinimumWidth()), widthMeasureSpec),
            resolveSize(minimumHeight.coerceAtLeast(super.getSuggestedMinimumHeight()), heightMeasureSpec)
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val thisView = this
        for (i in 0 until super.getChildCount()) {
            val childView = super.getChildAt(i)
            if (childView == null || childView.visibility == GONE) continue

            if (mRefreshContent == childView) {
                mRefreshContent?.let { contentView ->
                    val lp = contentView.layoutParams
                    val mlp = lp as? MarginLayoutParams
                    val leftMargin = mlp?.leftMargin ?: 0
                    val topMargin = mlp?.topMargin ?: 0

                    val left = leftMargin + thisView.paddingLeft
                    val top = topMargin + thisView.paddingTop
                    val right = left + contentView.measuredWidth
                    val bottom = top + contentView.measuredHeight

                    contentView.layout(left, top, right, bottom)
                }
            }

        }
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun onStartNestedScroll(child: View, target: View, axes: Int, type: Int): Boolean {
        return false
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int, type: Int) {
    }

    override fun onStopNestedScroll(target: View, type: Int) {
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int
    ) {
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray, type: Int) {
    }




    // SmartUtil
    object SmartUtil {

        fun isScrollableView(view: View?): Boolean {
            return view is AbsListView
                    || view is ScrollView
                    || view is ScrollingView
                    || view is WebView
                    || view is NestedScrollingChild
        }

        fun isContentView(view: View?): Boolean {
            return isScrollableView(view)
                    || view is ViewPager
                    || view is NestedScrollingParent
        }

    }

}
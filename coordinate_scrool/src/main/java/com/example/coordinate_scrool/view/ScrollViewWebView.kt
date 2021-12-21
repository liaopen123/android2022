package com.example.coordinate_scrool.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.webkit.WebView
import androidx.core.view.*

class NestedScrollWebView(val mContext:Context,attributeSet: AttributeSet) : WebView(mContext,attributeSet), NestedScrollingChild {
    private var mLastMotionY = 0f
    private val mScrollOffset = IntArray(2)
    private val mScrollConsumed = IntArray(2)
    private var mNestedYOffset = 0f
    private var mChildHelper: NestedScrollingChildHelper? = null

   init {
       init1()
   }

    private fun init1() {
        mChildHelper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        var result = false
        val trackedEvent: MotionEvent = MotionEvent.obtain(event)
        val action: Int = MotionEventCompat.getActionMasked(event)
        if (action == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0f
        }
        val y = event.getY().toInt()
        event.offsetLocation(0f, mNestedYOffset)
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mLastMotionY = y.toFloat()
                var nestedScrollAxis: Int = ViewCompat.SCROLL_AXIS_VERTICAL
                nestedScrollAxis = nestedScrollAxis or ViewCompat.SCROLL_AXIS_HORIZONTAL //按位或运算

//                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL);
                startNestedScroll(nestedScrollAxis)
                result = super.onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                var deltaY = mLastMotionY - y
                if (dispatchNestedPreScroll(0, deltaY.toInt(), mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1]
                    trackedEvent.offsetLocation(0F, mScrollOffset[1].toFloat())
                    mNestedYOffset += mScrollOffset[1]
                }
                val oldY: Int = getScrollY()
                mLastMotionY = (y - mScrollOffset[1]).toFloat()
                val newScrollY = Math.max(0f, oldY + deltaY)
                deltaY -= newScrollY - oldY
                if (dispatchNestedScroll(0, (newScrollY - deltaY).toInt(), 0, deltaY.toInt(), mScrollOffset)) {
                    mLastMotionY -= mScrollOffset[1]
                    trackedEvent.offsetLocation(0f, mScrollOffset[1].toFloat())
                    mNestedYOffset += mScrollOffset[1]
                }
                if (mScrollConsumed[1] == 0 && mScrollOffset[1] == 0) {
                    trackedEvent.recycle()
                    result = super.onTouchEvent(trackedEvent)
                }
            }
            MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                stopNestedScroll()
                result = super.onTouchEvent(event)
            }
        }
        return result
    }//设置嵌套滑动是否可用

    // NestedScrollingChild
    //嵌套滑动是否可用
   override fun isNestedScrollingEnabled(): Boolean {
        return mChildHelper!!.isNestedScrollingEnabled
    }

    /**
     * 开始嵌套滑动,
     *
     * @param axes 表示方向 有一下两种值
     * ViewCompat.SCROLL_AXIS_HORIZONTAL 横向滑动
     * ViewCompat.SCROLL_AXIS_VERTICAL 纵向滑动
     */
    override fun startNestedScroll(axes: Int): Boolean {
        return mChildHelper!!.startNestedScroll(axes)
    }

    override fun stopNestedScroll() { //停止嵌套滑动
        mChildHelper!!.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean { //是否有父View 支持 嵌套滑动,  会一层层的网上寻找父View
        return mChildHelper!!.hasNestedScrollingParent()
    }

    /**
     * 在处理滑动之后 调用
     *
     * @param dxConsumed     x轴上 被消费的距离
     * @param dyConsumed     y轴上 被消费的距离
     * @param dxUnconsumed   x轴上 未被消费的距离
     * @param dyUnconsumed   y轴上 未被消费的距离
     * @param offsetInWindow view 的移动距离
     * @return
     */
    override  fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        return mChildHelper!!.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    /**
     * 一般在滑动之前调用, 在ontouch 中计算出滑动距离, 然后调用该方法, 就给支持的嵌套的父View 处理滑动事件
     *
     * @param dx             x 轴上滑动的距离, 相对于上一次事件, 不是相对于 down事件的 那个距离
     * @param dy             y 轴上滑动的距离
     * @param consumed       一个数组, 可以传 一个空的 数组,  表示 x 方向 或 y 方向的事件 是否有被消费
     * @param offsetInWindow 支持嵌套滑动到额父View 消费 滑动事件后 导致 本 View 的移动距离
     * @return 支持的嵌套的父View 是否处理了 滑动事件
     */
    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return mChildHelper!!.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    /**
     * @param velocityX x 轴上的滑动速度
     * @param velocityY y 轴上的滑动速度
     * @param consumed  是否被消费
     * @return
     */
    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return mChildHelper!!.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    /**
     * @param velocityX x 轴上的滑动速度
     * @param velocityY y 轴上的滑动速度
     * @return
     */
    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mChildHelper!!.dispatchNestedPreFling(velocityX, velocityY)
    }

    companion object {
        val TAG = NestedScrollWebView::class.java.simpleName
    }
}
package com.lph.layoutmanager.layoutmanager

import androidx.recyclerview.widget.RecyclerView

class LPHLayoutManager:RecyclerView.LayoutManager() {
    private var mItemWidth: Int= 0
    private var mItemHeight: Int= 0
    private val visibleCount = 4

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onMeasure(
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State,
        widthSpec: Int,
        heightSpec: Int
    ) {
        if (state.itemCount==0) {
            super.onMeasure(recycler, state, widthSpec, heightSpec)
            return
        }
        if (state.isPreLayout) {
            return
        }
        //拿到第一个view  目的是用于得到单个item的宽高
        val itemView = recycler.getViewForPosition(0)
        addView(itemView)
        itemView.measure(widthSpec, heightSpec)
         mItemWidth = getDecoratedMeasuredWidth(itemView)
         mItemHeight = getDecoratedMeasuredWidth(itemView)

        detachAndScrapView(itemView, recycler)

        setWidthAndHeight(mItemWidth, mItemHeight)
    }

    /**
     * 设置recyclerView的整体的宽度和高度
     */
    private fun setWidthAndHeight(mItemWidth: Int, mItemHeight: Int) {

        setMeasuredDimension(mItemWidth*visibleCount, mItemHeight*visibleCount)

    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {

        detachAndScrapAttachedViews(recycler!!)

        var currentPosition = 0
        val totalSpace = width - paddingRight


        //开始填充view
        val view = recycler.getViewForPosition(currentPosition)
        addView(view)
        measureChild(view,0,0)
        layoutDecorated(view,0,0,mItemWidth,mItemHeight)




    }



}
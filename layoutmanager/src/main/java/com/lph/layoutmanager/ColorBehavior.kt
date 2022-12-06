package com.lph.layoutmanager

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.lph.layoutmanager.view.MoveView

class ColorBehavior(val context: Context, attrs: AttributeSet?) :
    CoordinatorLayout.Behavior<AppCompatImageView>(context, attrs) {

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: AppCompatImageView,
        dependency: View
    ): Boolean {
        return  dependency is MoveView
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: AppCompatImageView,
        dependency: View
    ): Boolean {
        child.setBackgroundColor(Color.BLUE)
        return super.onDependentViewChanged(parent, child, dependency)
    }
}
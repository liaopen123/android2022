package com.lph.livephotoview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.lph.livephotoview.databinding.ViewLivePhotoBinding
import com.lph.livephotoview.jz.JzvdLivePhotoStd.VisibleListener

class LivePhotoView  @JvmOverloads constructor(
    mContext: Context,
    attributeSet: AttributeSet? = null,
    style: Int = 0
) : FrameLayout(mContext, attributeSet, style) {
    private var viewbinding: ViewLivePhotoBinding = ViewLivePhotoBinding.inflate(
        LayoutInflater.from(mContext),
        this,
        true
    )
    fun initData(imgUrl: String?, videoUrl: String) {
        viewbinding.jzVideo.setUp("https://video.weibo.com/media/play?livephoto=https%3A%2F%2Fus.sinaimg.cn%2F000xl5cJgx080CkcMzpd0f0f0100rG5d0k01.mov","")
        viewbinding.jzVideo.startVideoAfterPreloading()
//        viewbinding.jzVideo.startVideo()
//        viewbinding.jzVideo.setImageVisibleListener(object :VisibleListener{
//            override fun visible1(hide:Boolean) {
//                if (hide) {
//                    viewbinding.cover.visibility = View.GONE
//                }else{
//                    viewbinding.cover.visibility = View.VISIBLE
//                }
//            }
//
//        })
    }
}
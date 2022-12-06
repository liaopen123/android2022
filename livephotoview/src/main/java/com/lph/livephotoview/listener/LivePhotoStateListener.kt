package com.lph.livephotoview.listener

import android.view.View

class LivePhotoStateListener {
    internal var toCircleDetailAction: ((position:Int) -> Unit)? = null
    internal var toUserProfileAction: ((position:Int) -> Unit)? = null
    internal var toShowDialogAction: ((position:Int) -> Unit)? = null
    internal var onPhotoClickAction: ((view: View) -> Unit)? = null
    internal var onPhotoClickActionPosition: ((position: Int) -> Unit)? = null
    internal var toPhotoDetailsAction: ((position:Int) -> Unit)? = null
    internal var onOperateClickedAction: ((position:Int) -> Unit)? = null
    internal var showGloryClickedAction: ((position:Int) -> Unit)? = null

    fun toCircleDetails(action: (position:Int) -> Unit) {
        toCircleDetailAction = action
    }
    fun toUserProfile(action: (position:Int) -> Unit) {
        toUserProfileAction = action
    }
    fun showDialogAction(action: (position:Int) -> Unit) {
        toShowDialogAction = action
    }
    fun photoClickAction(action: (view: View) -> Unit) {
        onPhotoClickAction = action
    }
    fun showPhotoDetailsAction(action: (position:Int) -> Unit) {
        toPhotoDetailsAction = action
    }

    fun onOperateClicked(action: (position:Int) -> Unit) {
        onOperateClickedAction = action
    }
    fun showGloryList(action: (position:Int) -> Unit){
        showGloryClickedAction = action
    }
    fun photoPositionClickAction(action: (position:Int) -> Unit){
        onPhotoClickActionPosition = action
    }




}
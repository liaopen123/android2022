package com.hhz.skin.app

import android.app.Application
import com.hhz.skin.SkinManager

class App:Application() {
    override fun onCreate() {
        SkinManager.init(this)
        super.onCreate()

    }
}
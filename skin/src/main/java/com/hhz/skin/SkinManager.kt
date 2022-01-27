package com.hhz.skin

import android.app.Application

class SkinManager private constructor(application: Application) {
    private val application: Application
    private val skinActivityLifecycle: SkinActivityLifecycle

    companion object {
        var instance: SkinManager? = null
            private set

        fun init(application: Application) {
            synchronized(SkinManager::class.java) {
                if (null == instance) {
                    instance = SkinManager(application)
                }
            }
        }
    }

    init {
        this.application = application
        //注册Activity生命周期回调
        skinActivityLifecycle = SkinActivityLifecycle()
        application.registerActivityLifecycleCallbacks(skinActivityLifecycle)
    }
}
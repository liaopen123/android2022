package com.example.floatwindow

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout

class FirstFloatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_float)
        val view = LayoutInflater.from(App.app.applicationContext).inflate(R.layout.float_view, null, false)


        App.app.registerActivityLifecycleCallbacks(object:Application.ActivityLifecycleCallbacks{
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
                (activity.window?.decorView as  FrameLayout).addView(view)
            }

            override fun onActivityPaused(activity: Activity) {
                (activity.window?.decorView as  FrameLayout).removeView(view)
            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
            }
        })
    }

    fun start(view: android.view.View) {
        startActivity(Intent(this,SecondFloatActivity::class.java))
    }
}
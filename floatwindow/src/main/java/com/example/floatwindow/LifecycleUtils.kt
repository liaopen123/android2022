package com.example.floatwindow

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * @author: liuzhenfeng
 * @function: 通过生命周期回调，判断系统浮窗的过滤信息，以及app是否位于前台，控制浮窗显隐
 * @date: 2019-07-11  15:51
 */
internal object LifecycleUtils {

    lateinit var application: Application
    private var activityCount = 0
    private var mTopActivity: WeakReference<Activity>? = null

    fun getTopActivity(): Activity? = mTopActivity?.get()

    fun setLifecycleCallbacks(application: Application) {
        this.application = application
        application.registerActivityLifecycleCallbacks(object :
            Application.ActivityLifecycleCallbacks {

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

            override fun onActivityStarted(activity: Activity) {
                // 计算启动的activity数目
                activity?.let { activityCount++ }
            }

            override fun onActivityResumed(activity: Activity) {
                activity.let {
                    mTopActivity?.clear()
                    mTopActivity = WeakReference<Activity>(it)
                    // 每次都要判断当前页面是否需要显示
                    checkShow(it)
                }
            }

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityStopped(activity: Activity) {
                activity.let {
                    // 计算关闭的activity数目，并判断当前App是否处于后台
                    activityCount--
                    checkHide(it)
                }
            }

            override fun onActivityDestroyed(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        })
    }

    /**
     * 判断浮窗是否需要显示
     */
    private fun checkShow(activity: Activity) {

    }

    /**
     * 判断浮窗是否需要隐藏
     */
    private fun checkHide(activity: Activity) {

    }

    fun isForeground() = activityCount > 0




}
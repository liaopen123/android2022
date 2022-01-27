package com.hhz.skin

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.view.LayoutInflaterCompat
import java.lang.Exception
import java.lang.reflect.Field

class SkinActivityLifecycle : Application.ActivityLifecycleCallbacks {
   override fun onActivityCreated(@NonNull activity: Activity, @Nullable savedInstanceState: Bundle?) {
        // 拿到对应的layoutInflater 创建skinLayoutFactory 并设置进去
        setFactory2(activity)
    }

    override fun onActivityStarted(activity: Activity) {

    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    /**
     * 监听到activity 生命周期设置Factory 来拦截系统的view创建
     * 需要注意的地方为 需要将mFactorySet置为false
     * 这里有个缺陷 ：>28 那么这个属性就不能使用反射来改变了 系统禁止了
     * 可以考虑直接反射来修改Factory的值 这个系统没有限制 这里没有实践
     */
    private fun setFactory2(activity: Activity) {
        val layoutInflater: LayoutInflater = LayoutInflater.from(activity)
        try {
            //Android 布局加载器 使用 mFactorySet 标记是否设置过Factory
            //如设置过抛出一次
            //设置 mFactorySet 标签为false
            val field: Field = LayoutInflater::class.java.getDeclaredField("mFactorySet")
            field.setAccessible(true)
            field.setBoolean(layoutInflater, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val skinLayoutFactory = SkinLayoutFactory()
        LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutFactory)
    }
}
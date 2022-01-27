package com.hhz.skin

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.view.LayoutInflaterCompat
import java.lang.Exception
import java.lang.reflect.Constructor

class SkinLayoutFactory :LayoutInflater.Factory2 {
    // 包目录列表
    private val sClassPrefixList = arrayOf(
        "android.widget.",
        "android.webkit.",
        "android.app.",
        "android.view."
    )

    // view构造方法的两个参数
    private val mConstructorSignature = arrayOf(Context::class.java, AttributeSet::class.java)

    // 用户缓存已经反射获得的构造方法，防止后续同一个类型的view重复反射
    private val sConstructorMap: HashMap<String, Constructor<out View?>> = HashMap<String, Constructor<out View?>>()
    //自定义View
    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {
// 创建view
        // 创建view
        val view: View? = createViewFromTag(context, name, attrs)
        Log.e("Skin1", "name = $name , view = $view")
        return view
    }
    //系统的View
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        Log.e("Skin2", "name = $name , view = $attrs")
           return createView(context,name,attrs)
    }


    private fun createViewFromTag(context: Context, name: String, attrs: AttributeSet): View? {
        var view: View? = if (-1 == name.indexOf('.')) {
            createViewByPkgList(context, name, attrs)
        } else {
            createView(context, name, attrs)
        }
        return view
    }



    /**
     * 通过遍历系统包来尝试创建view，如果上个没有创建成功有异常会被catch，然后继续尝试下一个包名来创建
     *
     * @param name 可能为TextView
     */
    private fun createViewByPkgList(context: Context, name: String, attrs: AttributeSet): View? {
        for (prefix in sClassPrefixList) {
            try {
                val view = createView(context, prefix + name, attrs)
                if (view != null) {
                    return view
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * 真正的开始创建view
     *
     * @param name name 格式为xxx.xxx.xxxView
     */
    private fun createView(context: Context, name: String, attrs: AttributeSet): View? {
        var constructor = sConstructorMap[name]
        if (null == constructor) {
            try {
                val aClass = context.classLoader.loadClass(name).asSubclass(View::class.java)
                constructor = aClass.getConstructor(*mConstructorSignature)
                sConstructorMap[name] = constructor
            } catch (e: Exception) {
            }
        }
        if (null != constructor) {
            try {
                return constructor.newInstance(context, attrs)
            } catch (e: Exception) {
            }
        }
        return null
    }




}
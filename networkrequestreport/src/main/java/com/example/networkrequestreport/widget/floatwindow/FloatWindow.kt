package com.example.networkrequestreport.widget.floatwindow

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.networkrequestreport.R
import com.example.networkrequestreport.report.NetworkReportQueue
import com.example.networkrequestreport.ui.network_detail.NetworkDetailsActivity

class FloatWindow {

    companion object {
        var app: Application? = null

        @SuppressLint("StaticFieldLeak")

        fun init(app: Application?) {
            this.app = app
        }


        fun start() {
            app?.apply {
                registerActivityLifecycleCallbacks(callback)
            }
        }


        val callback = object : Application.ActivityLifecycleCallbacks {
            var activityCount = 1
            private var mView: View? = null
            private var adapter1: FullRequestAdapter? = null
            private var mLayoutParams: ViewGroup.LayoutParams? = null

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

            }

            override fun onActivityStarted(activity: Activity) {
                activityCount++

            }

            override fun onActivityResumed(activity: Activity) {
                if (activity is NetworkDetailsActivity) {
                    return
                }
                if (mView == null) {
                    mView = createView(activity, mLayoutParams)
                }
                (activity.window?.decorView as FrameLayout).removeView(mView)
                (activity.window?.decorView as FrameLayout).addView(mView)
            }

            override fun onActivityPaused(activity: Activity) {
                mView?.apply {
                    mLayoutParams = (this as ViewGroup).getChildAt(0).layoutParams
                    (activity.window?.decorView as FrameLayout).removeView(this)
                }
                mView= null
            }

            override fun onActivityStopped(activity: Activity) {

            }

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                activityCount--
                Log.e("onActivityDestroyed", "${activity::class.java.simpleName} onActivityDestroyed,,,还剩下activityCount:$activityCount")
                //当没有activity以后  直接detach 防止内存泄漏
                if (activityCount < 0) {
                    detach(activity, mView)
                    mView = null
                }
            }


            fun createView(activity: Activity, layoutParams: ViewGroup.LayoutParams?): View {
                val view = LayoutInflater.from(activity).inflate(R.layout.float_view, null, false)
                layoutParams?.apply {
                    (view as ViewGroup).getChildAt(0).layoutParams = this
                }
                val rv = view?.findViewById<RecyclerView>(R.id.rv)
                val tv_name = view?.findViewById<TextView>(R.id.tv_name)
                tv_name?.text = activity::class.java.simpleName
                rv!!.apply {
                    layoutManager = LinearLayoutManager(rv.context).apply {
                        stackFromEnd = true
                    }
                    var queue = NetworkReportQueue.getQueue()
                    adapter1 = FullRequestAdapter(app!!, queue)
                    rv.adapter = adapter1
                }
                view.findViewById<View>(R.id.ivClose)?.setOnClickListener {
                    detach(activity, view)
                    mView = null
                }
                view.findViewById<View>(R.id.iv_clear)?.setOnClickListener {
                    NetworkReportQueue.clear()
                    adapter1?.notifyDataSetChanged()
                }
                adapter1?.setOnItemClickListener {
                    val intent = Intent(app, NetworkDetailsActivity::class.java)
                    intent.putExtra("position", it)
                    app?.startActivity(intent)
                }
                view.apply {
                    NetworkReportQueue.observe {
                        activity.runOnUiThread {
                            try {
                                adapter1?.notifyDataSetChanged()
                                rv.scrollToPosition(adapter1!!.itemCount - 1);
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                    }
                }
                return view
            }

            private fun detach(activity: Activity, view1: View?) {
                (activity.window?.decorView as FrameLayout).removeView(view1)
                app?.unregisterActivityLifecycleCallbacks(this)
                app = null
            }
        }
    }


}
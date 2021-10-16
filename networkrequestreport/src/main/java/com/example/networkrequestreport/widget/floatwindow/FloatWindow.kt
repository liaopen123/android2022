package com.example.networkrequestreport.widget.floatwindow

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.networkrequestreport.R
import com.example.networkrequestreport.report.NetworkReportQueue
import com.example.networkrequestreport.ui.network_detail.NetworkDetailsActivity

class FloatWindow {

    companion object{
        private  var adapter1: FullRequestAdapter?=null
        var app: Application?=null
        var view: View?=null
        fun init(app:Application){
            Companion.app = app
        }

        fun setContentView() {
             view = LayoutInflater.from(app?.applicationContext).inflate(R.layout.float_view, null, false)
            val rv = view?.findViewById<RecyclerView>(R.id.rv)

            rv!!.apply {
                layoutManager = LinearLayoutManager(rv.context)


                var queue = NetworkReportQueue.getQueue()
                 adapter1 = FullRequestAdapter(app!!,queue)


                rv.adapter = adapter1
            }
         view?.findViewById<View>(R.id.ivClose)?.setOnClickListener {
             NetworkReportQueue.clear()
             adapter1?.notifyDataSetChanged()
         }
            adapter1?.setOnItemClickListener {
                val intent = Intent(app, NetworkDetailsActivity::class.java)
                intent.putExtra("position",it)
                app?.startActivity(intent)
            }

        }
        fun start(){
            app?.apply {
                registerActivityLifecycleCallbacks(object:Application.ActivityLifecycleCallbacks{
                    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

                    }

                    override fun onActivityStarted(activity: Activity) {
                    }

                    override fun onActivityResumed(activity: Activity) {
                        if (activity is NetworkDetailsActivity) {
                            return
                        }
                        view?.apply {
                            (activity.window?.decorView as FrameLayout).addView(this)
                            NetworkReportQueue.observe {
                                activity.runOnUiThread {
                                    try {
                                        adapter1?.notifyDataSetChanged()
                                    }catch (e:Exception){
                                        e.printStackTrace()
                                    }
                                }

                            }
                        }
                    }

                    override fun onActivityPaused(activity: Activity) {
                        view?.apply {
                        (activity.window?.decorView as FrameLayout).removeView(this)
                        }
                    }

                    override fun onActivityStopped(activity: Activity) {

                    }

                    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
                    }

                    override fun onActivityDestroyed(activity: Activity) {
                    }
                })
            }
        }
    }


}
package com.example.networkrequestreport.report

import android.app.Application
import com.example.networkrequestreport.widget.floatwindow.FloatWindow

class NetWorkReport {

    companion object{
        var canReport = false
        var app:Application?=null

        fun startRecorder(){
            canReport =true
            FloatWindow.init(app)
            FloatWindow.start()
        }

        fun init(app: Application){
            this.app = app
        }

    }
}
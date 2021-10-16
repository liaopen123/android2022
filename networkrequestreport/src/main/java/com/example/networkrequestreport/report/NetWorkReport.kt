package com.example.networkrequestreport.report

import android.app.Application
import com.example.networkrequestreport.widget.floatwindow.FloatWindow

class NetWorkReport {

    companion object{
        var canReport = true

        fun startRecorder(app: Application){
            FloatWindow.init(app)
            FloatWindow.setContentView()
            FloatWindow.start()


        }

    }
}
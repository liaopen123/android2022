package com.example.networkrequestreport.report

import com.example.networkrequestreport.report.entity.NetworkQueue
import kotlin.collections.ArrayList

object NetworkReportQueue{
    private var notify: ((NetworkQueue) -> Unit)? = null
    private val reportQueue:ArrayList<NetworkQueue> by lazy{ ArrayList<NetworkQueue>() }



    fun getQueue(): ArrayList<NetworkQueue> {
        return reportQueue
    }
    fun add(networkQueue: NetworkQueue){
        reportQueue.add(networkQueue)
        notify?.invoke(networkQueue)
    }

    fun clear(){
        reportQueue.clear()
    }
    fun observe( notify: ((NetworkQueue) -> Unit)){
        this.notify = notify
    }


}
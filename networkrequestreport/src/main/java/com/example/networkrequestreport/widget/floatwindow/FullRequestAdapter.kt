package com.example.networkrequestreport.widget.floatwindow

import android.app.Application
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.networkrequestreport.R
import com.example.networkrequestreport.report.entity.NetworkQueue

class FullRequestAdapter(val app:Application,val list:ArrayList<NetworkQueue>?) : RecyclerView.Adapter<FullRequestAdapter.VH>() {
    private var itemClick: ((Int) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(app.applicationContext).inflate(R.layout.item_url, null, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.tv.text = list?.get(position)?.request?.url?:""
        holder.tv_time.text = list?.get(position)?.request?.startTime
        holder.itemView.setOnClickListener {
            itemClick?.invoke(position)
        }
    }

    override fun getItemCount()=list?.size?:0



    fun setOnItemClickListener(itemClick1: ((Int) -> Unit)){
        this.itemClick=itemClick1
    }
    class VH(val view:View) :RecyclerView.ViewHolder(view){
        val tv = view.findViewById<TextView>(R.id.tv_title)
        val tv_time = view.findViewById<TextView>(R.id.tv_time)

    }
}
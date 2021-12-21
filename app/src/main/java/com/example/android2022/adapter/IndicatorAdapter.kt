package com.example.android2022.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android2022.R

class IndicatorAdapter(private val mContext: Context) : RecyclerView.Adapter<IndicatorAdapter.IndicatorVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IndicatorVH {
        val view = LayoutInflater.from(mContext).inflate(R.layout.item_decoration_diary_drawer_menu, null, false)
        return IndicatorVH(view)
    }

    override fun onBindViewHolder(holder: IndicatorVH, position: Int) {
        if (position==0) {
            holder.topLine.visibility = View.INVISIBLE
        }else{
            holder.topLine.visibility = View.VISIBLE
        }
        if (position % 3 == 0) {
            holder.title.visibility = View.VISIBLE
            holder.iv_dot.visibility = View.VISIBLE
        }else{
            holder.title.visibility = View.GONE
            holder.iv_dot.visibility = View.INVISIBLE
        }


    }

    override fun getItemCount() = 7


    class IndicatorVH(item: View) : RecyclerView.ViewHolder(item) {
        val topLine = item.findViewById<View>(R.id.view_top_line)
        val line = item.findViewById<View>(R.id.view_line)
        val title = item.findViewById<TextView>(R.id.tv_progress_title)
        val iv_dot = item.findViewById<ImageView>(R.id.iv_dot)

    }
}
package com.example.floatwindow

import androidx.recyclerview.widget.RecyclerView


class RvUtils {

    companion object {

        fun loadData(rv: RecyclerView) {

            rv.layoutManager =
                androidx.recyclerview.widget.LinearLayoutManager(rv.context)
            val arrayList = ArrayList<String>()
            for (i in 1..100) {
                arrayList.add("" + i)
            }
            rv.adapter = CommonAdapter(arrayList)
        }


    }





}
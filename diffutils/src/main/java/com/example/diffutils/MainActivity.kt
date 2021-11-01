package com.example.diffutils

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil





class MainActivity : AppCompatActivity() {
    private var recyclerView: RecyclerView? = null
    private var mDatas: MutableList<PersonInfo>? = null
    private var personAdapter: PersonAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        initData()
        recyclerView!!.setLayoutManager(LinearLayoutManager(this))
        personAdapter = PersonAdapter(this, mDatas)
        recyclerView!!.setAdapter(personAdapter)
    }

    private fun initData() {
        mDatas = ArrayList()
        mDatas!!.add(PersonInfo(1, "姓名1"))
        mDatas!!.add(PersonInfo(2, "姓名2"))
        mDatas!!.add(PersonInfo(3, "姓名3"))
        mDatas!!.add(PersonInfo(4, "姓名4"))
        mDatas!!.add(PersonInfo(5, "姓名5"))
        mDatas!!.add(PersonInfo(6, "姓名6"))
        mDatas!!.add(PersonInfo(7, "姓名7"))
        mDatas!!.add(PersonInfo(8, "姓名8"))
        mDatas!!.add(PersonInfo(9, "姓名9"))
        mDatas!!.add(PersonInfo(10, "姓名10"))
        mDatas!!.add(PersonInfo(11, "姓名11"))
    }

    fun ADD(view: View?) {
//        val position = mDatas!!.size
//        val tempData: MutableList<PersonInfo> = ArrayList()
//        tempData.add(PersonInfo(12, "姓名12"))
//        tempData.add(PersonInfo(13, "姓名13"))
//        tempData.add(PersonInfo(14, "姓名114"))
//        mDatas!!.addAll(tempData)
//        personAdapter!!.notifyItemRangeInserted(position, tempData.size)

        val newData: MutableList<PersonInfo> = ArrayList()
        newData.addAll(mDatas!!)
        newData.add(PersonInfo(12, "姓名12"))
        newData.add(PersonInfo(13, "姓名13"))
        newData.add(PersonInfo(14, "姓名114"))
        DiffUtil.calculateDiff(DiffUtilCallBack(newData, mDatas), true).dispatchUpdatesTo(personAdapter!!)
        mDatas = newData
        personAdapter!!.setDatas(mDatas)

    }



    fun DELETE(view: View?) {
        mDatas!!.removeAt(1)
        personAdapter!!.notifyItemRemoved(1)
    }

    fun UPDATE(view: View?) {
        mDatas!![1].setName("姓名：我被更新了")
        personAdapter!!.notifyItemChanged(1)
    }

    fun UPDATE2(view: View?) {
        mDatas!![1].setName("姓名：我被更新了")
        val payload = Bundle()
        payload.putString("KEY_NAME", mDatas!![1].getName())
        personAdapter!!.notifyItemChanged(1, payload)
    }
}

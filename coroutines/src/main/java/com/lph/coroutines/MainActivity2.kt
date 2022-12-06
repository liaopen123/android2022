package com.lph.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*

class MainActivity2 : AppCompatActivity() {
    private val _etState = MutableStateFlow("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

//        main1()
//        Log.e("廖鹏辉","廖鹏辉:sssssssssssssssssssssssssssss")
//        et.doAfterTextChanged { text ->
//            // 往流里写数据
//            _etState.value = (text ?: "").toString()
//        }
//
//        lifecycleScope.launch {
//            _etState
//                .sample(5000) // 限流，500毫秒
//                .filter {
//                    // 空文本过滤掉
//                    it.isNotBlank()
//                }.collect() {
//                    // 订阅数据
//                    println("----------------->>> $it")
//                }
//        }

    }


    fun main1()= runBlocking {
        withContext(Dispatchers.IO){
            println("廖鹏辉 runBlocking:${Thread.currentThread().name}")
            val job = launch  (Dispatchers.IO) {
                println("廖鹏辉 launch:${Thread.currentThread().name}")
                simple().forEach { value -> println(value) }
            }
            launch {
                println("other operate")
            }

        }

    }

    suspend fun simple(): List<Int> {
        //模拟耗时操作
        println("廖鹏辉 simple:${Thread.currentThread().name}")
        delay(100000)
        return listOf(1, 2, 3)
    }

    fun gogogo(view: View) {
        val et = findViewById<EditText>(R.id.et)
        lifecycleScope.launch {
            val callbackFlow = callbackFlow {
                et.doAfterTextChanged {
                    trySend(it.toString())
                    close()
                }
                awaitClose{Log.e("廖鹏辉","关闭了")}
            }

            callbackFlow.collect{
                Log.e("廖鹏辉","得到的String:$it")
            }
        }
    }


}
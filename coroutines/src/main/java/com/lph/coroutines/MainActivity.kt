package com.lph.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class MainActivity : AppCompatActivity() {
    private val callRequestListDataFlow = MutableStateFlow("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        lifecycleScope.launch {
            callRequestListDataFlow.throttleFirstJava(3000).collect{
                println(it)
            }
        }


    }
    fun <T> Flow<T>.throttleFirst(duration: Long = 1000L) = this.throttleFirstImpl(duration)


    fun <T> Flow<T>.throttleFirstImpl(periodMillis: Long): Flow<T> {
        return flow {
            var lastTime = 0L
            collect { value ->
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastTime >= periodMillis) {
                    lastTime = currentTime
                    emit(value)
                }
            }
        }
    }
    fun <T> LiveData<T>.throttleLast(duration: Long = 1000L) = MediatorLiveData<T>().also { mld ->
        val source = this
        val handler = Handler(Looper.getMainLooper())
        val isUpdate = AtomicBoolean(true)

        val runnable = Runnable {
            if (isUpdate.compareAndSet(false,true)){
                mld.value = source.value
            }
        }

        mld.addSource(source) {
            if (isUpdate.compareAndSet(true,false)) {
                handler.postDelayed(runnable, duration)
            }
        }
    }

    fun <T> Flow<T>.throttleFirstJava(periodMillis: Long): Flow<T> {
        require(periodMillis > 0) { "period should be positive" }
        return flow {
            var lastTime = 0L
            collect { value ->
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastTime >= periodMillis) {
                    lastTime = currentTime
                    emit(value)
                }
            }
        }
    }

    fun doNoext(view: View) {
        callRequestListDataFlow.value="廖鹏辉1"
        callRequestListDataFlow.value="廖鹏辉2"
        callRequestListDataFlow.value="廖鹏辉3"
        callRequestListDataFlow.value="廖鹏辉4"
        callRequestListDataFlow.value="廖鹏辉5"
        callRequestListDataFlow.value="廖鹏辉6"
    }

}
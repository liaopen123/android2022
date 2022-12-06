package com.lph.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        runBlocking {
            val sample = flow {
                repeat(10) {
                    println("value::::$it")
                    emit(it)
                    delay(110)
                }
            }.sample(200).collect {
                println("----------------->>> $it")
            }
        }
    }

    @Test
    fun testFlow01() {
//        runBlocking {
//            flow<Int> {
//                emit(2)
//                emit(3)
//                println("廖鹏辉：我会执行的")
//            }.onEach {
//                println("廖鹏辉：$it")
//            }.collect()
//
////            task.collect()
//        }
        runBlocking {
//            (1..30).asFlow() // 转换为flow
//                .transform<Int,String> { request ->emit("廖鹏辉") }
//                .onEach {  println(it) }.collect()

//            (1..30).asFlow() // 转换为flow
//                .take(2)
//                .onEach {  println(it) }.collect()

//            val sum = (1..5).asFlow()
//                .map { it * it } //平方
//                .reduce { a,b->a+b}

//            flowOf(1,2,3).collectIndexed{index, value->
//                print(value)
//            }

//            flow {
//                emit(1)
//                delay(50)
//                emit(2)
//                delay(50)
//                emit("廖鹏辉23333")
//            } .collectLatest { value ->
//
//                println("Collecting $value")
//                delay(100) // Emulate work
//                println("$value collected")
//
//            }

//            val single = flow {
//                emit("廖鹏辉23333")
//                emit("廖鹏辉23333")
//                emit("廖鹏辉23333")
//            }.singleOrNull()
//            println("得到的值:$single")


//            val flow  = flow {
//                emit("廖鹏辉23333")
//                emit("廖鹏辉23333")
//                emit("廖鹏辉23333")
//            }
//            println("得到的值:${flow.count()}")

//            val sharedFlow = MutableSharedFlow<String>()
//            sharedFlow.emit("廖鹏辉23333")
//            sharedFlow.emit("廖鹏辉23333")
//            sharedFlow.emit("廖鹏辉23333")
//            sharedFlow.emit("廖鹏辉23333")
//
//            println("得到的值:${sharedFlow.count()}")
//
//            flow<Int> {
//                emit(1)
//                emit(2)
//                emit(3)
//            }.onEach {
//                println("廖鹏辉：$it")
//                //可以do 其他事情
//            }.collect{
//                println("廖鹏辉1：$it")
//            }

//            flow {
//                emit(3)
//                emit(1) //从此项开始不满足条件
//                emit(2)
//                emit(4)
//            }. takeWhile { it == 2  } .collect { value ->
//                println("廖鹏辉:$value")
//            }

//            flow {
//                emit(1)
//                emit(1) //从此项开始不满足条件
//                emit(2)
//                emit(3)
//                emit(3)
//                emit(4)
//
//            }. distinctUntilChangedBy{it} .collect { value ->
//                println("廖鹏辉:$value")
//            }
//            testGaojie{
//                getValueLiaopeng("里",it)
//            }
//
//            flow {
//                emit(1)
//                emit(2)
//                emit(3)
//                throw  java.lang.Exception("错错错错")
//            }.onCompletion{cause ->
//                if (cause != null)
//                    if (cause is Throwable) {
//                        cause.printStackTrace()
//                    }
//                    println("Flow completed exceptionally:${cause}")
//                    println("Done")
//
//            }.catch{
//                println("我catch住了${this}")
//            }  .collect {
//                println("廖鹏辉:$it")
//            }
//
//            callbackFlow {
//
//                trySend("廖鹏辉")
//            }
//        }


//            runBlocking {
//                val job = flowOf(1, 3, 5, 7).cancellable().onEach { value ->
//                    print(value)
//                }.launchIn(this)
//                job.cancel()
//            }

            testChannel()
            //取消


        }

        fun simple(): Flow<Int> = flow { // flow 构建
            for (i in 1..3) {
                //模拟异步耗时计算
                delay(100)
                //发射值
                emit(i)
            }
        }

        fun testGaojie(fangfa: (boolean: Boolean) -> Unit) {
            fangfa(true)
            fangfa(false)
        }


        fun getValueLiaopeng(value: String, test: Boolean) {
            if (test) {
                print("打印：$test,,$value")
            } else {
                print("不打印：$test,,$value")
            }

        }
    }


    private fun testChannel(){

        runBlocking {

            val channel = Channel<Int>()
            val producer = GlobalScope.launch {
                var i = 0
                while (true) {
                    delay(1000)
                    channel.send(++i)
                    println("发射:$i")
                }
            }


            val consumer = GlobalScope.launch {
                while (true) {
                    val receive = channel.receive()
                    println("接收:$receive")
                }
            }
//            joinAll(producer,consumer)
        }



    }
}
package com.lph.coroutines

import android.util.Log
import kotlinx.coroutines.*
import org.junit.Test
import kotlin.system.measureTimeMillis

class LaunchAndAsync {
    @Test
    fun test(){
        runBlocking {
            launch {
                delay(2000)
                println("廖鹏辉 launch")
            }

            val defferd = async {
                delay(2000)
                println("廖鹏辉 async")
                "hahah"
            }

            println(defferd.await())

        }
    }

    /**
     *        jobOne.await()  await的作用是  等待第一个有了结果 再执行下面的代码
     */
    @Test
    fun waitResult(){
        runBlocking {
            val jobOne =  async {
                delay(2000)
                println("one")
            }
            jobOne.await()
            val jobTwo =  async {
                delay(2000)
                println("two")
            }
            val jobThree =  async {
                delay(2000)
                println("three")
            }
        }
    }

    @Test
    fun waitResult1(){
        runBlocking {
          val jobOne =  launch {
                delay(2000)
                println("one")
            }
            jobOne.join()
            val jobTwo =  launch {
                delay(2000)
                println("two")
            }
            val jobThree =  launch {
                delay(2000)
                println("three")
            }
        }
    }

    suspend fun  taskOne ():Int{
        delay(1000)
        return 14
    }
    suspend fun  taskTwo ():Int{
        delay(1000)
        return 15
    }

    @Test
    fun getTotalTime(){
        runBlocking {
            val measureTimeMillis = measureTimeMillis {
                val jobOne = taskOne()
                val jobTwo = taskTwo()
                println("得到的结果:${jobOne + jobTwo}")
            }
            println("spend time:$measureTimeMillis")
        }
    }

    fun spend1000(){
        //如果每个方法耗时都是1000ms
        runBlocking {
            val task1 = async { taskOne() }
            val task2 = async { taskTwo() }
            println("得到的结果: ${task1.await()+task2.await()}")
            //上面的耗时则是1000ms

            val task3 = async { taskOne() }.await()
            val task4 = async { taskTwo() }.await()
            println("得到的结果: ${task3+task4}")
            //上面 3与4的耗时则是2000ms
        }
    }

}
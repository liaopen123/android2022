package com.hhz.test.exception

import android.annotation.SuppressLint
import android.content.Context
import com.google.gson.Gson
import com.hhz.test.exception.HzhuFileManager.DEBUG_EXTERNAL_PATH
import com.hhz.test.exception.HzhuFileManager.writeFile
import com.hzhu.m.exception.ExceptionBean

import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 *
 * author:honlin
 * date :2020/07/28
 *
 */

// TODO: 2020/7/28 读本地表 ---》遍历数据 ----》判断文件是否存在 ---》存在上报----》成功后删除
// 上报信息分为立即上报 和 延时上报 两种、
// 立即上报为不影响app正常使用的 例如慢方法、io异常 等
// 延时上报的为NPE等运行时异常

object ExceptionUtils {

    const val TAG = "ExceptionUtils"


    private const val ERROR_LOG = "/Trace"
    private const val ERROR_LOG_TYPE = ".txt"

    const val TypeCrash = 1
    const val TypeIO = 2
    const val TypeEvilMethod = 3
    const val TypeStartUp = 4
    const val TypeMemory = 5
    const val TypeJson = 6


    /**
     * 异常写入txt
     * */
    @SuppressLint("SimpleDateFormat")
    fun writeException(context: Context?, exception: String, type: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss")
        val errorDir: String = if (context == null) {
            DEBUG_EXTERNAL_PATH + ERROR_LOG
        } else {
            context.cacheDir.absolutePath + ERROR_LOG
        }
        val fileName = "${type}_" + System.currentTimeMillis() + ERROR_LOG_TYPE
        val errorPath = "$errorDir/$fileName"
        var deviceInfo = ""
        val date = dateFormat.format(Date())
        val file = File(errorDir, fileName)
        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }
        val content = Gson().toJson(
            ExceptionBean(deviceInfo, exception, date, arrayListOf()
               , "")
        )
        writeFile(errorPath, content, false)

        return errorPath
    }




}

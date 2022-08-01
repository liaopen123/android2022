package com.hhz.test.exception

import android.os.Environment
import java.io.Closeable
import java.io.File
import java.io.FileWriter
import java.io.IOException
import kotlin.jvm.Throws

object HzhuFileManager {

    val DEBUG_EXTERNAL_PATH = Environment.getExternalStorageDirectory().path + "/haohaozhu"
    val CACHE_PIC_PATH = "/Pic"

    /**
     * write file
     *
     * @param filePath
     * @param content
     * @param append   is append, if true, write to the end of file, else clear content of file and write into it
     * @return return true
     * @throws IOException if an error occurs while operator FileWriter
     */
    fun writeFile(filePath: String, content: String, append: Boolean): Boolean {
        var fileWriter: FileWriter? = null
        try {
            fileWriter = FileWriter(filePath, append)
//            content.replace("\t","\t ")
            fileWriter.write(content)
            content.split("\n").forEach {
                if ("\n" == it) {
                    fileWriter.write(System.getProperty("line.separator"))
                }else{
                    fileWriter.write(it)
                }
            }
            fileWriter.close()
            return true
        } catch (e: IOException) {
            throw RuntimeException("IOException occurred. ", e)
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close()
                } catch (e: IOException) {
                    throw RuntimeException("IOException occurred. ", e)
                }

            }
        }
    }

    fun checkDir(rootPath: File, fileName: String): Boolean {
        val file = File(rootPath, fileName)
        val parentFile = file.parentFile
        return if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs()
        } else false
    }

    fun close(inputStream: Closeable?) {
        if (inputStream != null) {
            try {
                inputStream.close()
            } catch (e: Throwable) {
                e.printStackTrace()
                // do nothing
            }
        }
    }



    fun getSavePicDir(): String? {
        var finalPath = ""
        val DIR = "haohaozhu"
        val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), DIR)
        return if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            ""
        } else {
            finalPath = mediaStorageDir.absolutePath
            finalPath
        }
    }

    @Throws(Exception::class)
    fun checkFile(file: File): Boolean {
        val parent = file.parentFile
        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                return false
            }
        }
        if (file.exists()) {
            if (!file.delete()) {
                return false
            }
        }
        return file.createNewFile()
    }
}

package com.example.networkrequestreport.utils

object BlackListUtils {
    private val urlBlackList = arrayOf<String>("stat","resshow")

    fun contain(url: String): Boolean {
        for (keywords in urlBlackList) {
            if (url.lowercase().contains(keywords)){
                return true
            }

        }
        return false
    }

}
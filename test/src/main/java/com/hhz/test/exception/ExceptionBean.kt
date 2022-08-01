package com.hzhu.m.exception

/**
 *
 * author:honlin
 * date :2020/7/30
 *
 */
class ExceptionBean(val baseInfo: String, val exception: String, val date: String, val stack: ArrayList<String>, val x5msg: String) {
    override fun toString(): String {
        return "ExceptionBean(baseInfo='$baseInfo', exception='$exception', date='$date', stack=$stack,x5msg='$x5msg')"
    }
}
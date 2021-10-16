package com.example.networkrequestreport.report.entity

import android.annotation.SuppressLint
import com.example.networkrequestreport.report.intercept.internal.isProbablyUtf8
import okhttp3.Headers
import okhttp3.Interceptor
import okio.Buffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class ReportRequest(chain: Interceptor.Chain) : IRequest {


    private val request = chain.request()
    private val connection = chain.connection()
    private val requestBody = request.body()

    @SuppressLint("SimpleDateFormat")
    override fun startTime(): String {
        val time = System.currentTimeMillis()
        val format = SimpleDateFormat("yyyy年MM月dd日-hh时mm分ss秒")
        val date = Date(time)
        return format.format(date)
    }

    override fun protocol() = "${connection?.protocol() ?: ""}"

    override fun url() = "${request.url()}"

    override fun method() = request.method()

    override fun contentLength() = "${requestBody?.contentLength()}-byte"

    override fun contentType() = "${requestBody?.contentType()}-byte"

    override fun body(): String {
        if (requestBody == null) {
            return "error requestBody null"
        } else if (bodyHasUnknownEncoding(request.headers())) {
            return "--> END ${request.method()} (encoded body omitted)"
        } else if (requestBody.isDuplex()) {
            return "--> END ${request.method()} (duplex request body omitted)"
        } else {
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            val contentType = requestBody.contentType()
            val charset: Charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8
            return if (buffer.isProbablyUtf8()) {
                buffer.readString(charset)
            } else {
                "--> END ${request.method()} (binary ${requestBody.contentLength()}-byte body omitted)"
            }
        }
    }

    override fun headers(): HashMap<String, String> {
        return parseHeaders(request.headers())
    }

   }
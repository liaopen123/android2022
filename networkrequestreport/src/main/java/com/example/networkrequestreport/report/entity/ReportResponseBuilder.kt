package com.example.networkrequestreport.report.entity

import com.example.networkrequestreport.report.intercept.internal.isProbablyUtf8
import okhttp3.Response
import okio.Buffer
import okio.GzipSource
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class ReportResponseBuilder(private val response: Response, private val tookTime: Long) : IResponse {
    private val responseBody = response.body()!!

    override fun code() = response.code()

    override fun message() = if (response.message().isEmpty()) "" else ' ' + response.message()

    override fun tookTime() = tookTime

    override fun url() = "${response.request().url()}"

    override fun method() = response.request().method()

    override fun contentLength() = "${responseBody.contentLength()}"

    override fun contentType() = "${responseBody.contentType()}"

    override fun body(): String {
        if (bodyHasUnknownEncoding(response.headers())) {
            return "<-- END HTTP (encoded body omitted)"
        } else {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            var buffer = source.buffer

            var gzippedLength: Long? = null
            if ("gzip".equals(response.headers()["Content-Encoding"], ignoreCase = true)) {
                gzippedLength = buffer.size()
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }

            val contentType = responseBody.contentType()
            val charset: Charset = contentType?.charset(StandardCharsets.UTF_8) ?: StandardCharsets.UTF_8

            if (!buffer.isProbablyUtf8()) {
                return "<-- END HTTP (binary ${buffer.size()}-byte body omitted)"
            }

            if (responseBody.contentLength() != 0L) {
                return buffer.clone().readString(charset)
            }
            if (gzippedLength != null) {
                return "<-- END HTTP (${buffer.size()}-byte, $gzippedLength-gzipped-byte body)"
            } else {
                return "<-- END HTTP (${buffer.size()}-byte body)"
            }
        }
    }

    override fun headers(): HashMap<String, String> {
        return parseHeaders(response.headers())
    }

    fun build()= ReportResponse(code(),tookTime(),message(),url(),method(),contentLength(),contentType(),body(),headers())

}
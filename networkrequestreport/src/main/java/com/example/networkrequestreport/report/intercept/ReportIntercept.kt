package com.example.networkrequestreport.report.intercept


import com.example.networkrequestreport.report.NetWorkReport
import com.example.networkrequestreport.report.NetworkReportQueue
import com.example.networkrequestreport.report.entity.NetworkQueue
import com.example.networkrequestreport.report.entity.ReportRequest
import com.example.networkrequestreport.report.entity.ReportResponse
import com.example.networkrequestreport.utils.BlackListUtils
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit


class ReportIntercept @JvmOverloads constructor() : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        if (!NetWorkReport.canReport|| BlackListUtils.contain("${request.url()}")) {
            return chain.proceed(request)
        }
        val reportRequest = ReportRequest(chain)
        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        val reportResponse = ReportResponse(response, tookMs)
        //add and dispatch
        NetworkReportQueue.add(NetworkQueue(reportRequest, reportResponse))
        return response
    }


}

package com.example.networkrequestreport.report.entity


interface IRequest :CommonPart{
    fun startTime():String  //年月日
    fun protocol():String // * [Protocol.HTTP_1_0].
}
interface IResponse:CommonPart{
    fun code():Int
    fun message():String
    fun tookTime():Long
    fun bodySize():String


}

interface Headers{
    fun headers() :HashMap<String,String>


    fun parseHeaders(headers: okhttp3.Headers):HashMap<String, String> {
        val headerMap = HashMap<String, String>()
        for (i in 0 until headers.size()) {
            val name = headers.name(i)
            // Skip headers from the request body as they are explicitly logged above.
            if (!"Content-Type".equals(name, ignoreCase = true) &&
                !"Content-Length".equals(name, ignoreCase = true)
            ) {
                val value = if (headers.name(i) in emptySet<String>()) "██" else headers.value(i)
                headerMap[headers.name(i)] = value
            }
        }
        return headerMap
    }


    fun bodyHasUnknownEncoding(headers: okhttp3.Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }
}

interface CommonPart :Headers{
    fun url():String
    fun method():String
    fun contentLength():String
    fun contentType():String
    fun body():String
}


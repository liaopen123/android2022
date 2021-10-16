package com.example.networkrequestreport.report.entity


data class ReportResponse(val code:Int, val tookTime: Long,val message:String,val url:String,val method:String,val contentLength:String,val contentType:String,val params:String,val headers: HashMap<String, String>)
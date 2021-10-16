package com.example.networkrequestreport.report.entity


data class ReportRequest(val startTime: String,val protocol:String,val url:String,val method:String,val contentLength:String,val contentType:String,val params:String,val headers: HashMap<String, String> )
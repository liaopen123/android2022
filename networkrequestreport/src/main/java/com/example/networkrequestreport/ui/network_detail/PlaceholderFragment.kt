package com.example.networkrequestreport.ui.network_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.networkrequestreport.R
import com.example.networkrequestreport.report.entity.ReportRequest
import com.example.networkrequestreport.report.entity.ReportResponse
import com.yuyh.jsonviewer.library.JsonRecyclerView
import kotlinx.android.synthetic.main.fragment_main.*


/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment(val request: ReportRequest?,val response: ReportResponse?) : Fragment() {
    constructor(request: ReportRequest) : this(request,null)
    constructor(response: ReportResponse) : this(null,response)



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tv_url = view.findViewById<TextView>(R.id.tv_url)
        val tv_headers = view.findViewById<TextView>(R.id.tv_headers)
        val tv_time_value = view.findViewById<TextView>(R.id.tv_time_value)
        val tv_code = view.findViewById<TextView>(R.id.tv_code)
        val tv_protocol = view.findViewById<TextView>(R.id.tv_protocol)
        val tv_method = view.findViewById<TextView>(R.id.tv_method)
        val tv_contentLength = view.findViewById<TextView>(R.id.tv_contentLength)
        val tv_contentType = view.findViewById<TextView>(R.id.tv_contentType)
        val tv_params = view.findViewById<TextView>(R.id.tv_params)
            val ll_params = view.findViewById<LinearLayout>(R.id.ll_params)

        request?.apply {
            tv_url.text = url
            tv_headers.text = headers.toString()
            tv_time_value.text =  startTime
            tv_protocol.text =  protocol
            tv_method.text =  method
            tv_contentLength.text =   contentLength
            tv_contentType.text =  contentType
            tv_params.text =  params
        }

        response?.apply {
            tv_url.text = url
            tv_headers.text = headers.toString()
            tv_time_title.text = "耗时:"
            tv_time_value.text ="${tookTime}毫秒"
            tv_code.text = "status:"
            tv_protocol.text =  "${code}"
            tv_method.text =  method
            tv_contentLength.text =   contentLength
            tv_contentType.text =  contentType
            ll_params.visibility = View.GONE
            val mRecyclewView = view.findViewById<JsonRecyclerView>(R.id.rv_json)
            mRecyclewView.bindJson(params)
            mRecyclewView.setTextSize(20f)
        }




    }
}
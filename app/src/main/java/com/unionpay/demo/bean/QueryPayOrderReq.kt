package com.unionpay.demo.bean

class QueryPayOrderReq {
    var mer_order_no = "" //  商户订单号必填
    var merchant_no = "" // 商户号
    var request_no = "" // 求流水号 必填
    var request_time = ""  // 请求时间(yyyyMMddHHmmss) 必填
    override fun toString(): String {
        return "QueryPayOrderReq(mer_order_no='$mer_order_no', merchant_no='$merchant_no', request_no='$request_no', request_time='$request_time')"
    }
}
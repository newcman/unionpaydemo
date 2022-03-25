package com.unionpay.demo.bean

class QueryPayOrderRes {
    var accounting_day = "" // 会计日
    var amount = 0.00F //  订单总金额，单位为元
    var mer_order_no = "" //   商户订单号
    var order_no = "" //  易联订单号
    var profit_share_result = "" //  分账结果
    var sub_merchant_no = "" //   子商户号
    var req_reserved = "" //  商户保留域
    var pay_status = 1 // 订单状态
    override fun toString(): String {
        return "QueryPayOrderRes(accounting_day='$accounting_day', amount=$amount, mer_order_no='$mer_order_no', order_no='$order_no', profit_share_result='$profit_share_result', sub_merchant_no='$sub_merchant_no', req_reserved='$req_reserved', pay_status=$pay_status)"
    }
}
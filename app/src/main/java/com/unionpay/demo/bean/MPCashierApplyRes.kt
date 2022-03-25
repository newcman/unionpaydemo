package com.unionpay.demo.bean
/**
 * 小程序收银台
 */
class MPCashierApplyRes {
    var accounting_day = "" // 会计日
    var merch_order_no = "" //   商户订单号
    var order_no = "" //  易联订单号
    var profit_share_result = "" //  分账结果
    var sub_merchant_no = "" //   子商户号
    var req_reserved = "" //  商户保留域
    var url = "" //  商户保留域
    override fun toString(): String {
        return "MPCashierApplyRes(accounting_day='$accounting_day', merch_order_no='$merch_order_no', order_no='$order_no', profit_share_result='$profit_share_result', sub_merchant_no='$sub_merchant_no', req_reserved='$req_reserved', url='$url')"
    }
}
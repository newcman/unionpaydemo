package com.unionpay.demo.bean

/**
 * 小程序收银台
 */
class MPCashierApplyReq {
    var amount = 0.00F //  订单总金额，单位为元
    var entry_type = "03"//   进入小程序方式(01：APP通过scheme跳转，02：扫码，03:OpenSDK)
    var merch_order_no = "" //  商户订单号必填
    var merchant_no = "" // 商户号
    var request_no = "" // 求流水号 必填
    var request_time = ""  // 请求时间(yyyyMMddHHmmss) 必填
    var order_desc = "" // 订单描述
    override fun toString(): String {
        return "MPCashierApplyReq(amount=$amount, entry_type='$entry_type', merch_order_no='$merch_order_no', merchant_no='$merchant_no', request_no='$request_no', request_time='$request_time', order_desc='$order_desc')"
    }
}
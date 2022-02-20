package com.unionpay.uppayplugin.demo.bean

/**
 * 订单请求
 */
class ResultData<T> {
    var code = ""
    var data: T? = null
    var merchant_no = ""
    var msg = ""//

    var response_time = ""
    var sign = ""
    override fun toString(): String {
        return "ResultData(code='$code', data=$data, merchant_no='$merchant_no', msg='$msg', response_time='$response_time', sign='$sign')"
    }
}
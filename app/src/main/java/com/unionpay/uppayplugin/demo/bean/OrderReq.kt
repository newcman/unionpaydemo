package com.unionpay.uppayplugin.demo.bean

/**
 * 订单请求
 */
class OrderReq {
    var aggregateModel = "" //  聚合模式：01-标准模式；02-直通模式
    var amount = 0.00F //  订单总金额，单位为元
    var ebankEnAbbr = "" //  统一收银台直通模式的银行标识
    var ebankType = ""//   个人网关类别(01:APP+H5 02:APP 03:H5)

    var merchOrderNo = "" //  商户订单号必填
    var merchantNo = "" //  商户号  必填
    var requestNo = "" // 求流水号 必填
    var requestTime = ""  // 请求时间(yyyyMMddHHmmss) 必填
    override fun toString(): String {
        return "OrderReq(aggregateModel='$aggregateModel', amount=$amount, ebankEnAbbr='$ebankEnAbbr', ebankType='$ebankType', merchOrderNo='$merchOrderNo', merchantNo='$merchantNo', requestNo='$requestNo', requestTime='$requestTime')"
    }
}
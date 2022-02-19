package com.unionpay.uppayplugin.demo.api

import retrofit2.http.POST

/**
 * 订单接口
 */
interface IOrderApi {
    @POST
    suspend fun makeOrder():String
}
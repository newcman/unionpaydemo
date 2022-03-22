package com.unionpay.demo.api

import com.unionpay.demo.bean.OrderRes
import com.unionpay.demo.bean.ResultData
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 订单接口
 */
interface IOrderApi {
    @POST("/receipt-app-demo/demo/unioncounter/placeorder")
    suspend fun makeOrder(@Body requestBody: RequestBody): ResultData<OrderRes>
}
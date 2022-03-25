package com.unionpay.demo.api

import com.unionpay.demo.bean.MPCashierApplyRes
import com.unionpay.demo.bean.OrderRes
import com.unionpay.demo.bean.QueryPayOrderRes
import com.unionpay.demo.bean.ResultData
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 订单接口
 */
interface IOrderApi {
    // 银联收银台
    @POST("/receipt-app-demo/demo/unioncounter/placeorder")
    suspend fun makeOrder(@Body requestBody: RequestBody): ResultData<OrderRes>

    // 小程序收银台
    @POST("/receipt-app-demo/demo/miniProgramCashier/apply")
    suspend fun makeWxOrder(@Body requestBody: RequestBody): ResultData<MPCashierApplyRes>

    @POST("/receipt-app-demo/demo/unify/queryPayOrder")
    suspend fun queryWxOrder(@Body requestBody: RequestBody): ResultData<QueryPayOrderRes>
}
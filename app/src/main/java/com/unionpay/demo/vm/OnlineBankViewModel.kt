package com.unionpay.demo.vm

import android.app.Activity
import android.util.Log
import com.ccb.ifpaysdk.message.PayResultListener
import com.ccb.ifpaysdk.pay.CCBIfPaySDK


/**
 * 网银网关viewmodel
 */
class OnlineBankViewModel : BaseViewModel() {
    // 支付结果
    val payResultListener = PayResultListener { resultMap ->
        Log.i(TAG, "---支付返回结果---$resultMap")
    }

    override suspend fun doPay(activity: Activity) {
        //实例化对象并初始化相关数据
        val params = ""
        val ccbNetPay = CCBIfPaySDK.Builder() //商户APP当前上下文对象
            .setActivity(activity) //支付回调对象
            .setListener(payResultListener) //商户串，详情参考商户串配置
            .setParams(params)
            .build()
        ccbNetPay.pay() //调起支付方法

    }
}
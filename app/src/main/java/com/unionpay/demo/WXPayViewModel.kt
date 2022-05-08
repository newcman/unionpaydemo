package com.unionpay.demo

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.unionpay.demo.api.IOrderApi
import com.unionpay.demo.bean.*
import com.unionpay.demo.bean.Constant.Companion.PAGE_PAY
import com.unionpay.demo.bean.Constant.Companion.PAGE_PAY_RESULT
import com.unionpay.demo.bean.Constant.Companion.RESULT_OK
import com.unionpay.demo.ssl.TrustAllSSLSocketFactory
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*


/**
 * 微信支付viewmodel
 */
class WXPayViewModel : ViewModel() {
    companion object {
        private const val TAG = "WXPayViewModel"
    }

    val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val pageLiveData: MutableLiveData<Int> = MutableLiveData(0)
    private var retrofit: Retrofit? = null
    private var api: IOrderApi? = null
    private val mSimpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss")
    var orderRes: MPCashierApplyRes? = null
    var orderReq: MPCashierApplyReq? = null
    var payResult = QueryPayOrderRes.PayStatus.NOT_PAY
    val okHttpClient = OkHttpClient.Builder().sslSocketFactory(
        TrustAllSSLSocketFactory.newInstance(),
        TrustAllSSLSocketFactory.TrustAllCertsManager()
    )

    /**
     *
     *<item>测试环境</item>
    <item>开发环境</item>
    <item>联调环境</item>
    <item>生产环境</item>
     * 开发环境地址：https://openapi.d.payeco.com/receipt-app-demo
    测试环境地址：https://openapi.t.payeco.com/receipt-app-demo
    联调环境地址：https://openapi.test.payeco.com/receipt-app-demo
    生产环境地址：https://openapi.payeco.com/receipt-app-demo
     */
    fun initApi(env: Int) {
        val baseUrl = when (env) {
            0 -> "https://openapi.t.payeco.com"
            1 -> "https://openapi.d.payeco.com"
            2 -> "https://openapi.test.payeco.com"
            else -> "https://openapi.payeco.com"
        }
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))//使用rxjava是加上这一句
            .build()
        api = retrofit?.create(IOrderApi::class.java)
        Log.d(TAG, "initApi $baseUrl")
    }


    /**
     */
    fun pay(context: Context, orderReq: MPCashierApplyReq, miniprogramType: Int) {
        orderRes = null
        this.orderReq = orderReq
        viewModelScope.launch {
            loadingLiveData.value = true
            var resultData: ResultData<MPCashierApplyRes>?
            try {
                resultData = makeOrder(orderReq)
                loadingLiveData.value = false
                Log.d(TAG, "resultData $resultData")
                if (RESULT_OK != resultData?.code) {
                    Toast.makeText(context, "下单失败：${resultData?.msg}", Toast.LENGTH_LONG).show()
                    return@launch
                }
            } catch (e: Exception) {
                Toast.makeText(context, "下单失败：${e.message}", Toast.LENGTH_LONG).show()
                loadingLiveData.value = false
                Log.d(TAG, "下单失败：${e.message}", e)
                return@launch
            }
            orderRes = resultData.data
            val url = orderRes?.url
            if (TextUtils.isEmpty(url)) {
                Toast.makeText(context, "获取到的url为空", Toast.LENGTH_LONG).show()
                return@launch
            }
            jumpWxApplet(context, url!!, miniprogramType)
        }
    }


    private suspend fun makeOrder(orderReq: MPCashierApplyReq): ResultData<MPCashierApplyRes> {
        Log.d(TAG, "makeOrder $orderReq")
        val json: MediaType = MediaType.parse("application/json; charset=utf-8")!!
        orderReq.request_time = mSimpleDateFormat.format(Date())
        val gson = Gson()
        val orderJson = gson.toJson(orderReq)
        Log.d(TAG, "orderJson $orderJson")
        val body = RequestBody.create(json, orderJson)
        return api!!.makeWxOrder(body)
    }

    /**
     * 结果界面
     */
    fun queryPayResult() {
        orderReq?.apply {
            Log.d(TAG, "onPayResult $orderReq")
            viewModelScope.launch {
                val queryReq = QueryPayOrderReq()
                queryReq.merchant_no = merchant_no
                queryReq.mer_order_no = merch_order_no
                queryReq.request_no = "${System.currentTimeMillis()}"
                val queryResult = queryOrder(queryReq)
                Log.d(TAG, "queryResult $queryResult")
                payResult = QueryPayOrderRes.PayStatus.of(queryResult.data?.pay_status)
                pageLiveData.value = PAGE_PAY_RESULT
            }
        }
    }

    /**
     * 返回
     */
    fun back() {
        orderReq = null
        pageLiveData.value = PAGE_PAY
    }

    /**
     * 跳转到微信小程序
     */
    fun jumpWxApplet(context: Context, path: String, miniprogramType: Int) {
        val appId = Constant.WX_APP_ID // 填移动应用(App)的 AppId，非小程序的 AppID

        val api = WXAPIFactory.createWXAPI(context, appId)
        if (!api.isWXAppInstalled) {
            Toast.makeText(context, "微信未安装，请先安装微信", Toast.LENGTH_LONG).show()
            return
        }
        val req = WXLaunchMiniProgram.Req()
        req.userName = Constant.WX_SOURCE_APP_ID // 填小程序原始id

        req.path = path ////拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。

        req.miniprogramType = miniprogramType // 可选打开 开发版，体验版和正式版

        api.sendReq(req)
    }

    /**
     * 查询订单
     */
    private suspend fun queryOrder(orderReq: QueryPayOrderReq): ResultData<QueryPayOrderRes> {
        Log.d(TAG, "queryOrder $orderReq")
        val json: MediaType = MediaType.parse("application/json; charset=utf-8")!!
        orderReq.request_time = mSimpleDateFormat.format(Date())
        val gson = Gson()
        val orderJson = gson.toJson(orderReq)
        Log.d(TAG, "orderJson $orderJson")
        val body = RequestBody.create(json, orderJson)
        return api!!.queryWxOrder(body)
    }
}
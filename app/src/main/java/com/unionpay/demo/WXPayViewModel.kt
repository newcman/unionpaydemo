package com.unionpay.demo

import android.content.Context
import android.content.Intent
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
import com.unionpay.demo.bean.Constant.Companion.PAY_CANCEL
import com.unionpay.demo.bean.Constant.Companion.PAY_FAILED
import com.unionpay.demo.bean.Constant.Companion.PAY_SUCCESS
import com.unionpay.demo.bean.Constant.Companion.RESULT_OK
import com.unionpay.demo.ssl.TrustAllSSLSocketFactory
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
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
        private const val TAG = "PayViewModel"
    }

    val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val pageLiveData: MutableLiveData<Int> = MutableLiveData(0)
    private var retrofit: Retrofit
    private var api: IOrderApi
    private val mSimpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss")
    var orderRes: MPCashierApplyRes? = null
    var orderReq: MPCashierApplyReq? = null
    var payResult = PAY_SUCCESS

    init {
        val okHttpClient = OkHttpClient.Builder().sslSocketFactory(
            TrustAllSSLSocketFactory.newInstance(),
            TrustAllSSLSocketFactory.TrustAllCertsManager()
        )
        retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.payeco.com")
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))//使用rxjava是加上这一句
            .build()
        api = retrofit.create(IOrderApi::class.java)
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
//            jumpWxApplet(context, url!!, miniprogramType)

            val queryReq = QueryPayOrderReq()
            queryReq.merchant_no = orderReq.merchant_no
            queryReq.mer_order_no = orderReq.merch_order_no
            queryReq.request_no = "${System.currentTimeMillis()}"
            val queryResult = queryOrder(queryReq)
            Log.d(TAG, "queryResult $queryResult")
            payResult = queryResult.data?.pay_status ?: PAY_SUCCESS
            pageLiveData.value = PAGE_PAY_RESULT

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
        return api.makeWxOrder(body)
    }


    fun onPayResult(data: Intent?) {
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         ************************************************/
        /*************************************************
         * 步骤3：处理银联手机支付控件返回的支付结果
         */
        if (data == null) {
            payResult = PAY_FAILED
            pageLiveData.value = PAGE_PAY_RESULT
            return
        }

        var msg = ""
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        /*
         * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
         */
        val str = data.extras!!.getString("pay_result")
        Log.d(TAG, "onPayResult $str")
        when {
            str.equals("success", ignoreCase = true) -> {
                // 如果想对结果数据验签，可使用下面这段代码，但建议不验签，直接去商户后台查询交易结果
                // result_data结构见c）result_data参数说明
                if (data.hasExtra("result_data")) {
                    val result = data.extras!!.getString("result_data")
                    try {
                        val resultJson = JSONObject(result)
                        val sign = resultJson.getString("sign")
                        val dataOrg = resultJson.getString("data")

                        Log.d(TAG, "onPayResult sign=$str , dataOrg=$data")
                        // 此处的verify建议送去商户后台做验签
                        // 如要放在手机端验，则代码必须支持更新证书
                        //                    val ret: Boolean = verify(dataOrg, sign, mMode)
                        //                    msg = if (ret) {
                        //                        // 验签成功，显示支付结果
                        //                        "支付成功！"
                        //                    } else {
                        //                        // 验签失败
                        //                        "支付失败！"
                        //                    }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
                // 结果result_data为成功时，去商户后台查询一下再展示成功
                msg = "支付成功！"
                payResult = PAY_SUCCESS
            }
            str.equals("fail", ignoreCase = true) -> {
                msg = "支付失败！"
                payResult = PAY_FAILED
            }
            str.equals("cancel", ignoreCase = true) -> {
                msg = "用户取消了支付"
                payResult = PAY_CANCEL
            }
        }
        pageLiveData.value = PAGE_PAY_RESULT
    }

    /**
     * 返回
     */
    fun back() {
        pageLiveData.value = PAGE_PAY
    }

    /**
     * 跳转到微信小程序
     */
    fun jumpWxApplet(context: Context, path: String, miniprogramType: Int) {
        val appId = Constant.WX_APP_ID // 填移动应用(App)的 AppId，非小程序的 AppID

        val api = WXAPIFactory.createWXAPI(context, appId)
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
        return api.queryWxOrder(body)
    }
}
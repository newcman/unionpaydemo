package com.unionpay.demo.vm

import android.app.Activity
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unionpay.demo.PayViewModel
import com.unionpay.demo.api.IOrderApi
import com.unionpay.demo.bean.Constant
import com.unionpay.demo.bean.Constant.Companion.PAGE_PAY
import com.unionpay.demo.bean.Constant.Companion.PAY_SUCCESS
import com.unionpay.demo.bean.OrderInfo
import com.unionpay.demo.bean.OrderRes
import com.unionpay.demo.bean.ResultData
import com.unionpay.demo.ssl.TrustAllSSLSocketFactory
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat

enum class PayStatus(val status: Int, val desc: String) {
    NOT_PAY(0, "未支付"),
    PAYING(200, "支付中"),
    PAY_SUCCESS(400, "支付成功"),
    PAY_FAILED(600, "支付失败"),
    UNKNOWN(-1, "未知状态");

    companion object {
        fun of(status: Int?): PayStatus {
            return values().find { it.status == status } ?: UNKNOWN
        }
    }
}

/**
 * 支付viewmodel
 */
open class BaseViewModel : ViewModel() {
    companion object {
        const val TAG = "BaseViewModel"
    }

    val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val pageLiveData: MutableLiveData<Int> = MutableLiveData(0)
    private var retrofit: Retrofit? = null
    private var api: IOrderApi? = null
    private val mSimpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss")
    var payResult = PayStatus.NOT_PAY
    var orderInfo: OrderInfo? = null
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
     * 下单并支付
     */
    fun makeOrderAndPay(activity: Activity, orderReq: Any) {
        viewModelScope.launch {
            loadingLiveData.value = true
            makeOrder()
            doPay(activity)
        }
    }


    /**
     * 下订单
     */
    open suspend fun makeOrder() {

    }

    /**
     * 去支付
     */
    open suspend fun doPay(activity: Activity) {

    }

    /**
     * 返回
     */
    fun back() {
        pageLiveData.value = PAGE_PAY
    }

    fun queryPayResult() {

    }
}
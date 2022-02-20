package com.unionpay.uppayplugin.demo

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.unionpay.UPPayAssistEx
import com.unionpay.uppayplugin.demo.api.IOrderApi
import com.unionpay.uppayplugin.demo.bean.Constant.Companion.MODEL_NORMAL
import com.unionpay.uppayplugin.demo.bean.Constant.Companion.PAGE_PAY
import com.unionpay.uppayplugin.demo.bean.Constant.Companion.PAGE_PAY_RESULT
import com.unionpay.uppayplugin.demo.bean.Constant.Companion.PAY_CANCEL
import com.unionpay.uppayplugin.demo.bean.Constant.Companion.PAY_FAILED
import com.unionpay.uppayplugin.demo.bean.Constant.Companion.PAY_SUCCESS
import com.unionpay.uppayplugin.demo.bean.Constant.Companion.RESULT_OK
import com.unionpay.uppayplugin.demo.bean.OrderReq
import com.unionpay.uppayplugin.demo.bean.OrderRes
import com.unionpay.uppayplugin.demo.bean.ResultData
import com.unionpay.uppayplugin.demo.ssl.TrustAllSSLSocketFactory
import com.unionpay.uppayplugin.demo.util.PayBrand
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineExceptionHandler
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
 * 支付viewmodel
 */
class PayViewModel : ViewModel() {
    companion object {
        private const val TAG = "PayViewModel"
    }

    val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    val pageLiveData: MutableLiveData<Int> = MutableLiveData(0)
    private var retrofit: Retrofit
    private var api: IOrderApi
    private val mSimpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss")
    var orderRes: OrderRes? = null
    var orderReq: OrderReq? = null
    var payResult = PAY_SUCCESS

    val handler = CoroutineExceptionHandler { context, exception ->
        Log.e(TAG, "error $exception")
    }

    init {
        val okHttpClient = OkHttpClient.Builder().sslSocketFactory(
            TrustAllSSLSocketFactory.newInstance(),
            TrustAllSSLSocketFactory.TrustAllCertsManager()
        )
        retrofit = Retrofit.Builder()
            .baseUrl("https://openapi.t.payeco.com")
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))//使用rxjava是加上这一句
            .build()
        api = retrofit.create(IOrderApi::class.java)
    }

    /**
     * 支付
     * 1、生成订单，获取到TN
     * 2、调用支付界面
     * 3、处理支付结果
     */
    fun pay(context: Context, orderReq: OrderReq, env: String) {
        orderRes = null

        val setype = PayBrand.getSeType()
        Log.d(TAG, "setype $setype")
        this.orderReq = orderReq
        viewModelScope.launch {
            loadingLiveData.value = true
            var resultData: ResultData<OrderRes>?
            try {
                resultData = makeOrder(orderReq)
                loadingLiveData.value = false
                Log.d(TAG, "resultData $resultData")
                if (resultData?.code != RESULT_OK) {
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
            val tn = orderRes?.tn
            if (TextUtils.isEmpty(tn)) {
                Toast.makeText(context, "获取到的tn为空", Toast.LENGTH_LONG).show()
                return@launch
            }
            startPay(context, tn!!, env, orderReq.aggregateModel)
        }
    }


    private suspend fun makeOrder(orderReq: OrderReq): ResultData<OrderRes> {
        Log.d(TAG, "makeOrder $orderReq")
        val json: MediaType = MediaType.parse("application/json; charset=utf-8")!!
        orderReq.requestTime = mSimpleDateFormat.format(Date())
        val gson = Gson()
        val orderJson = gson.toJson(orderReq)
        Log.d(TAG, "orderJson $orderJson")
        val body = RequestBody.create(json, orderJson)
        return api.makeOrder(body)
    }

    /**
     */
    private suspend fun startPay(
        context: Context,
        tn: String,
        env: String,
        aggregateModel: String
    ): Int {
        Log.d(TAG, "startPay tn=$tn env=$env aggregateModel=$aggregateModel")

        /**
         * 参数说明： context —— 用于获取启动支付控件的活动对象的context
         * spId —— 保留使用，这里输入null
         * sysProvider —— 保留使用，这里输入null
         * orderInfo —— 订单信息为交易流水号，即TN，为商户后台从银联后台获取。
         * mode —— 银联后台环境标识，“00”将在银联正式环境发起交易,“01”将在银联测试环境发起
         * 交易seType —— 手机pay支付类别，见表1
         */
        if (aggregateModel == MODEL_NORMAL) { // 普通模式
            return UPPayAssistEx.startPay(context, null, null, tn, env)
        } else {
            val setype = PayBrand.getSeType()
            Log.d(TAG, "setype $setype")
            return UPPayAssistEx.startSEPay(context, null, null, tn, env, setype)
        }
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
}
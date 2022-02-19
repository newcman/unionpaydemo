package com.unionpay.uppayplugin.demo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unionpay.uppayplugin.demo.api.IOrderApi
import com.unionpay.uppayplugin.demo.ssl.TrustAllSSLSocketFactory
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 支付viewmodel
 */
class PayViewModel : ViewModel() {
    val loadingLiveData: MutableLiveData<Boolean> = MutableLiveData(false)
    private lateinit var retrofit: Retrofit
    private lateinit var api: IOrderApi

    init {
        val okHttpClient = OkHttpClient.Builder().sslSocketFactory(
                TrustAllSSLSocketFactory.newInstance(),
                TrustAllSSLSocketFactory.TrustAllCertsManager()
        )
        retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
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
    fun pay() {
        viewModelScope.launch {
            loadingLiveData.value = true
            val tn = makeOrder()
            loadingLiveData.value = false
            startPay()
        }
    }


    suspend fun makeOrder(): String {
        return ""
    }

    suspend fun startPay(): String {
        return ""
    }
}
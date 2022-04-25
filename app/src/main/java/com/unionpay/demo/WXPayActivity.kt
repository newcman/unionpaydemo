package com.unionpay.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.payeco.wallet.R
import com.unionpay.demo.bean.Constant.Companion.PAGE_PAY
import com.unionpay.demo.bean.Constant.Companion.PAGE_PAY_RESULT
import com.unionpay.demo.ui.WXPayFragment
import com.unionpay.demo.ui.WXPayResultFragment

/**
 * 微信小程序支付
 */
class WXPayActivity : AppCompatActivity() {
    private val mViewModel: WXPayViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(WXPayViewModel::class.java)
    }
    private var mPayFragment: WXPayFragment? = null
    private var mPayResultFragment: WXPayResultFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wx_pay_activity)
        if (savedInstanceState == null) {
            mPayFragment = WXPayFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, mPayFragment!!)
                .commitNow()
        }

        Log.d("mViewModel", "mViewModel $mViewModel")
        initViewModel()
    }

    private fun initViewModel() {
        mViewModel?.pageLiveData?.observe(this, Observer<Int> {
            when (it) {
                PAGE_PAY -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, mPayFragment!!)
                        .commitNow()
                }
                PAGE_PAY_RESULT -> {
                    mPayResultFragment = WXPayResultFragment.newInstance()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, mPayResultFragment!!)
                        .commitNow()
                }
                else -> {

                }
            }

        })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        mViewModel?.onPayResult(data)
    }
}
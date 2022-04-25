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
import com.unionpay.demo.ui.PayFragment
import com.unionpay.demo.ui.PayResultFragment
import java.lang.reflect.Proxy
import kotlin.concurrent.thread

/**
 * 银联支付
 */
class UnionPayActivity : AppCompatActivity() {
    private val mViewModel: PayViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(PayViewModel::class.java)
    }
    private var mPayFragment: PayFragment? = null
    private var mPayResultFragment: PayResultFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.union_pay_activity)
        if (savedInstanceState == null) {
            mPayFragment = PayFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, mPayFragment!!)
                .commitNow()
        }

        Log.d("mViewModel", "mViewModel $mViewModel")
        initViewModel()

        Proxy.newProxyInstance(
            classLoader, arrayOf(Runnable::class.java)
        ) { proxy, method, args ->
            {
                Log.d("xs", "method ${method.name}")
            }
        }

        thread {
            Log.d("xs", "1111")
        }
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
                    mPayResultFragment = PayResultFragment.newInstance()
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
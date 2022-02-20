package com.unionpay.uppayplugin.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.unionpay.uppayplugin.demo.bean.Constant.Companion.PAGE_PAY
import com.unionpay.uppayplugin.demo.bean.Constant.Companion.PAGE_PAY_RESULT
import com.unionpay.uppayplugin.demo.ui.PayFragment
import com.unionpay.uppayplugin.demo.ui.PayResultFragment
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    private var mViewModel: PayViewModel? = null
    private var mPayFragment: PayFragment? = null
    private var mPayResultFragment: PayResultFragment? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        mViewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(PayViewModel::class.java)
        if (savedInstanceState == null) {
            mPayFragment = PayFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, mPayFragment!!)
                .commitNow()
        }

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
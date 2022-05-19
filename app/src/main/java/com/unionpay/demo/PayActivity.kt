package com.unionpay.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.payeco.wallet.R
import com.unionpay.demo.bean.Constant.Companion.PAGE_PAY
import com.unionpay.demo.bean.Constant.Companion.PAGE_PAY_RESULT
import com.unionpay.demo.factory.FactoryManager
import com.unionpay.demo.factory.IFactory
import com.unionpay.demo.ui.BasePayFragment
import com.unionpay.demo.ui.BasePayResultFragment
import com.unionpay.demo.vm.BaseViewModel
import kotlinx.android.synthetic.main.pay_activity.*
import kotlinx.android.synthetic.main.wx_pay_activity.iv_back

/**
 * 统一支付界面
 */
class PayActivity : AppCompatActivity() {
    companion object {
        const val CHANNEL_ONLINE_BANK = 1 // 网银网关
        const val CHANNEL = "channel"
        fun startPayActivity(context: Context, channel: Int) {
            val intent = Intent()?.apply {
                setClass(context, PayActivity::class.java)
                putExtra(CHANNEL, channel)
            }
            context.startActivity(intent)
        }
    }

    private  var mViewModel: BaseViewModel? = null
    private var mFactory: IFactory? = null
    private var mPayFragment: BasePayFragment? = null
    private var mPayResultFragment: BasePayResultFragment? = null
    private val mChannel = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pay_activity)
        val channel = intent.getIntExtra(CHANNEL, CHANNEL_ONLINE_BANK)
        mFactory = FactoryManager.getFactory(channel)
        if (savedInstanceState == null) {
            mPayFragment = mFactory!!.createPayFragment(this)
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, mPayFragment!!)
                .commitNow()
        }

        initViewModel()
        mPayFragment?.mViewModel = mViewModel
        Log.d("mViewModel", "mViewModel $mViewModel")
        tv_title.text = mFactory?.getTitle()
        iv_back.setOnClickListener {
            finish()
        }
    }

    private fun initViewModel() {
        mViewModel = mFactory?.createViewModel(this)
        mViewModel?.pageLiveData?.observe(this, Observer<Int> {
            when (it) {
                PAGE_PAY -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, mPayFragment!!)
                        .commitNow()
                }
                PAGE_PAY_RESULT -> {
                    mPayResultFragment =  mFactory!!.createPayResultFragment(this)
                    mPayResultFragment?.mViewModel = mViewModel
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.container, mPayResultFragment!!)
                        .commitNow()
                }
                else -> {

                }
            }

        })
    }


    override fun onResume() {
        super.onResume()
        mViewModel?.queryPayResult()
    }
}
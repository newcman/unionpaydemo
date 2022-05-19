package com.unionpay.demo.ui

import android.view.View
import android.widget.AdapterView
import com.payeco.wallet.R
import kotlinx.android.synthetic.main.online_bank_pay_fragment.*

/**
 * 小程序支付界面
 */
class OnlineBankPayFragment : BasePayFragment() {
    override fun getLayoutID() = R.layout.online_bank_pay_fragment

    override fun initView() {
        super.initView()
        // 付款银行
        sp_pay_bank.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
//                    0 -> miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST
//                    1 -> miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
//                    2 -> miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
                    else -> {

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        // 受理渠道
        sp_pay_channel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
//                    0 -> miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_TEST
//                    1 -> miniprogramType = WXLaunchMiniProgram.Req.MINIPROGRAM_TYPE_PREVIEW
//                    2 -> miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
                    else -> {

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }
}
package com.unionpay.demo.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.unionpay.demo.PayViewModel
import com.unionpay.demo.R
import com.unionpay.demo.bean.Constant
import kotlinx.android.synthetic.main.pay_result_fragment.*

/**
 * 支付结果界面
 */
class WXPayResultFragment : Fragment() {
    private val mViewModel: PayViewModel by lazy {
        ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory()).get(
            PayViewModel::class.java
        )
    }

    companion object {
        fun newInstance(): WXPayResultFragment {
            return WXPayResultFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.wx_pay_result_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("mViewModel", "mViewModel $mViewModel")
        initView()
    }

    private fun initView() {
        bt_back.setOnClickListener {
            mViewModel.back()
        }

        mViewModel.apply {
            when (payResult) {
                Constant.PAY_SUCCESS -> {
                    iv_pay_result.setImageResource(R.mipmap.icon_pay_ok)
                    tv_pay_result.text = "支付成功"
                }
                Constant.PAY_FAILED -> {
                    iv_pay_result.setImageResource(R.mipmap.icon_pay_failed)
                    tv_pay_result.text = "支付失败"
                }
                Constant.PAY_CANCEL -> {
                    iv_pay_result.setImageResource(R.mipmap.icon_pay_failed)
                    tv_pay_result.text = "支付取消"
                }
            }


            var amount = orderReq?.amount ?: 0F

            orderRes?.apply {
                tv_order_amount?.text = String.format("%.2f", amount) + "元"
                tv_merchant_order_id.text = merch_order_no
                tv_bank_order_id.text = order_no
            }
        }
    }

}
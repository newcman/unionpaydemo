package com.unionpay.demo.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.payeco.wallet.R
import com.unionpay.demo.WXPayViewModel
import com.unionpay.demo.bean.Constant
import com.unionpay.demo.bean.QueryPayOrderRes
import kotlinx.android.synthetic.main.pay_result_fragment.*
import kotlinx.android.synthetic.main.pay_result_fragment.bt_back
import kotlinx.android.synthetic.main.pay_result_fragment.iv_pay_result
import kotlinx.android.synthetic.main.pay_result_fragment.tv_merchant_order_id
import kotlinx.android.synthetic.main.pay_result_fragment.tv_order_amount
import kotlinx.android.synthetic.main.pay_result_fragment.tv_pay_result
import kotlinx.android.synthetic.main.wx_pay_result_fragment.*

/**
 * 支付结果界面
 */
class WXPayResultFragment : Fragment() {
    private val mViewModel: WXPayViewModel by lazy {
        ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory()).get(
            WXPayViewModel::class.java
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
        Log.d("WXPayResultFragment", "mViewModel $mViewModel")
        initView()
    }

    private fun initView() {
        bt_back.setOnClickListener {
            if (mViewModel.payResult == QueryPayOrderRes.PayStatus.PAYING) {
                mViewModel.queryPayResult()
            } else {
                mViewModel.back()
            }
        }

        tv_copy_merchant_order_id.setOnClickListener {
            Toast.makeText(context, "复制成功", Toast.LENGTH_LONG).show()
            val clipboardManager =
                context!!.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipboardManager?.setPrimaryClip(
                ClipData.newPlainText(
                    "商户订单号",
                    tv_merchant_order_id.text.toString()
                )
            )
        }

        tv_copy_yilian_orderr_id.setOnClickListener {
            Toast.makeText(context, "复制成功", Toast.LENGTH_LONG).show()
            val clipboardManager =
                context!!.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
            clipboardManager?.setPrimaryClip(
                ClipData.newPlainText(
                    "易联订单号",
                    tv_yilian_order_id.text.toString()
                )
            )
        }

        mViewModel.apply {
            when (payResult) {
                QueryPayOrderRes.PayStatus.PAY_SUCCESS -> {
                    bt_back.text = "返回"
                    iv_pay_result.setImageResource(R.mipmap.icon_pay_ok)
                }
                QueryPayOrderRes.PayStatus.PAYING -> {
                    bt_back.text = "查询支付结果"
                    iv_pay_result.setImageResource(R.mipmap.icon_paying)
                }
                else -> {
                    bt_back.text = "返回"
                    iv_pay_result.setImageResource(R.mipmap.icon_pay_failed)
                }
            }
            tv_pay_result.text = payResult.desc

            var amount = orderReq?.amount ?: 0F

            orderRes?.apply {
                tv_order_amount?.text = String.format("%.2f", amount) + "元"
                tv_yilian_order_id.text = merch_order_no
                tv_merchant_order_id.text = order_no
            }

            orderReq?.apply {
                tv_merchant_id.text = merchant_no
            }
        }
    }

}
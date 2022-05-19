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
import com.unionpay.demo.PayViewModel
import com.unionpay.demo.WXPayViewModel
import com.unionpay.demo.bean.Constant
import com.unionpay.demo.bean.QueryPayOrderRes
import com.unionpay.demo.vm.BaseViewModel
import com.unionpay.demo.vm.PayStatus
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
abstract class BasePayResultFragment : Fragment() {
    private companion object {
        private const val TAG = "BasePayResultFragment"
    }

    var mViewModel: BaseViewModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutID(), container, false)
    }

    abstract fun getLayoutID(): Int

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "mViewModel $mViewModel")
        initView()
    }

    private fun initView() {
        bt_back.setOnClickListener {
            if (mViewModel?.payResult == PayStatus.PAYING) {
                mViewModel?.queryPayResult()
            } else {
                mViewModel?.back()
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

        mViewModel?.apply {
            when (payResult) {
                PayStatus.PAY_SUCCESS -> {
                    bt_back.text = "返回"
                    iv_pay_result.setImageResource(R.mipmap.icon_pay_ok)
                }
                PayStatus.PAYING -> {
                    bt_back.text = "查询支付结果"
                    iv_pay_result.setImageResource(R.mipmap.icon_paying)
                }
                else -> {
                    bt_back.text = "返回"
                    iv_pay_result.setImageResource(R.mipmap.icon_pay_failed)
                }
            }
            tv_pay_result.text = payResult.desc

            orderInfo?.apply {
                tv_order_amount?.text = String.format("%.2f", amount) + "元"
                tv_yilian_order_id.text = ylOrderNo
                tv_merchant_order_id.text = merchOrderNo
                tv_merchant_id.text = ylNo
            }
        }
    }
}
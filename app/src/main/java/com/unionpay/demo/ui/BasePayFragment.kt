package com.unionpay.demo.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.payeco.wallet.R
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.unionpay.demo.PayViewModel
import com.unionpay.demo.WXPayViewModel
import com.unionpay.demo.bean.Constant.Companion.ENV_PRODUCT
import com.unionpay.demo.bean.Constant.Companion.ENV_TEST
import com.unionpay.demo.bean.Constant.Companion.MERCHANT_NO_PRODUCT
import com.unionpay.demo.bean.Constant.Companion.MERCHANT_NO_TEST
import com.unionpay.demo.bean.MPCashierApplyReq
import com.unionpay.demo.bean.OrderReq
import com.unionpay.demo.bean.QueryPayOrderReq
import com.unionpay.demo.vm.BaseViewModel
import kotlinx.android.synthetic.main.wx_pay_fragment.*
import kotlinx.coroutines.launch

/**
 * 小程序支付界面
 */
abstract class BasePayFragment : Fragment() {
    private companion object {
        private const val TAG = "BasePayFragment"
    }

    private var mLoadingDialog: ProgressDialog? = null
    var mViewModel: BaseViewModel? = null
    private var env = ENV_TEST // 环境

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
        initViewModel()
        initView()
    }


    private fun initViewModel() {
        mViewModel?.loadingLiveData?.observe(viewLifecycleOwner, Observer<Boolean> {
            if (it) {
                mLoadingDialog = ProgressDialog.show(
                    context,  // context
                    "",  // title
                    "正在下单,请稍候...",  // message
                    true
                ) // 进度是否是不确定的，这只和创建进度条有关
            } else {
                mLoadingDialog?.dismiss()
            }
        })
    }

    open fun initView() {
        //  环境
        sp_env.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                when (position) {
                    3 -> {
                        tv_merchant_id.setText(MERCHANT_NO_PRODUCT)
                        env = ENV_PRODUCT
                    }
                    else -> {
                        tv_merchant_id.setText(MERCHANT_NO_TEST)
                        env = ENV_TEST
                    }
                }
                mViewModel?.initApi(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        sp_env.setSelection(0)
        bt_make_order.setOnClickListener {
            val order = check()
            if (order != null) {
                mViewModel?.makeOrderAndPay(activity!!, order)
            }
        }
    }

    open fun check(): Any? {
        val merchantNo = tv_merchant_id.text.toString().trim()
        if (TextUtils.isEmpty(merchantNo)) {
            Toast.makeText(context, "请输入商户号", Toast.LENGTH_LONG).show()
            return null
        }
        var amount = 0.0F
        try {
            val amountStr = tv_order_amount.text.toString().trim()
            if (!TextUtils.isEmpty(amountStr)) {
                amount = amountStr.toFloat()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (amount < 0.00001) {
            Toast.makeText(context, "请输入订单金额", Toast.LENGTH_LONG).show()
            return null
        }

        var orderDesc = tv_order_desc.text.toString().trim()
        if (TextUtils.isEmpty(orderDesc)) {
            Toast.makeText(context, "请输入订单描述", Toast.LENGTH_LONG).show()
            return null
        }
        return true
    }
}
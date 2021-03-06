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
import com.payeco.wallet.R
import com.unionpay.demo.PayViewModel
import com.unionpay.demo.bean.Constant.Companion.ENV_PRODUCT
import com.unionpay.demo.bean.Constant.Companion.ENV_TEST
import com.unionpay.demo.bean.Constant.Companion.MERCHANT_NO_PRODUCT
import com.unionpay.demo.bean.Constant.Companion.MERCHANT_NO_TEST
import com.unionpay.demo.bean.Constant.Companion.MODEL_DIREC
import com.unionpay.demo.bean.Constant.Companion.MODEL_NORMAL
import com.unionpay.demo.bean.OrderReq
import kotlinx.android.synthetic.main.pay_fragment.*
import kotlinx.android.synthetic.main.pay_fragment.sp_env
import kotlinx.android.synthetic.main.pay_fragment.tv_merchant_id
import kotlinx.android.synthetic.main.pay_fragment.tv_order_amount
import kotlinx.android.synthetic.main.wx_pay_fragment.*
import java.lang.Exception

/**
 * 支付界面
 */
class PayFragment : Fragment() {
    private var mLoadingDialog: ProgressDialog? = null
    private val mViewModel: PayViewModel by lazy {
        ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory()).get(
            PayViewModel::class.java
        )
    }
    private var aggregateModel = MODEL_NORMAL // 聚合模式：01-标准模式；02-直通模式
    private var env = ENV_TEST // 环境

    companion object {
        fun newInstance(): PayFragment {
            return PayFragment()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.pay_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Log.d("mViewModel", "mViewModel $mViewModel")
        initViewModel()
        initView()
    }


    private fun initViewModel() {
        mViewModel?.loadingLiveData?.observe(viewLifecycleOwner, Observer<Boolean> {
            if (it) {
                mLoadingDialog = ProgressDialog.show(
                    context,  // context
                    "",  // title
                    "正在努力的获取tn中,请稍候...",  // message
                    true
                ) // 进度是否是不确定的，这只和创建进度条有关
            } else {
                mLoadingDialog?.dismiss()
            }
        })
    }

    private fun initView() {

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
        //  聚合模式
        sp_mode.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> aggregateModel = MODEL_NORMAL
                    1 -> aggregateModel = MODEL_DIREC
                    else -> {

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

        bt_pay.setOnClickListener {
            val merchantNo = tv_merchant_id.text.toString().trim()
            if (TextUtils.isEmpty(merchantNo)) {
                Toast.makeText(context, "请输入商户号", Toast.LENGTH_LONG).show()
                return@setOnClickListener
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
                return@setOnClickListener
            }

            var ebankEnAbbr = tv_bank_index.text.toString().trim()

            if (aggregateModel == MODEL_DIREC && TextUtils.isEmpty(ebankEnAbbr)) {
                Toast.makeText(context, "直通模式下请输入直播银行标识", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val orderReq = OrderReq()
            orderReq.aggregateModel = aggregateModel
            orderReq.amount = amount
            orderReq.ebankEnAbbr = ebankEnAbbr
            orderReq.ebankType = "02"
            orderReq.merchOrderNo = "${System.currentTimeMillis()}"
            orderReq.merchantNo = merchantNo
            orderReq.requestNo = "${System.currentTimeMillis()}"
            mViewModel?.pay(context!!, orderReq, env)
        }
    }
}
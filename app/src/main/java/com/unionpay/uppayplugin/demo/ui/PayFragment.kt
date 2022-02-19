package com.unionpay.uppayplugin.demo.ui

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.unionpay.uppayplugin.demo.PayViewModel
import com.unionpay.uppayplugin.demo.R

/**
 * 支付界面
 */
class PayFragment : Fragment() {
    private var mLoadingDialog: ProgressDialog? = null
    private var mViewModel: PayViewModel? = null

    companion object {
        fun newInstance(): PayFragment {
            return PayFragment()
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProvider(requireActivity(),ViewModelProvider.NewInstanceFactory()).get(PayViewModel::class.java)
        initViewModel()
    }


    private fun initViewModel() {
        mViewModel?.loadingLiveData?.observe(this, Observer<Boolean> {
            if (it) {
                mLoadingDialog = ProgressDialog.show(context,  // context
                        "",  // title
                        "正在努力的获取tn中,请稍候...",  // message
                        true) // 进度是否是不确定的，这只和创建进度条有关
            } else {
                mLoadingDialog?.dismiss()
            }
        })
    }
}
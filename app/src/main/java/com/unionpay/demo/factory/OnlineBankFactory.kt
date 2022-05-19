package com.unionpay.demo.factory

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.unionpay.demo.ui.BasePayFragment
import com.unionpay.demo.ui.BasePayResultFragment
import com.unionpay.demo.ui.OnlineBankPayFragment
import com.unionpay.demo.ui.OnlineBankPayResultFragment
import com.unionpay.demo.vm.BaseViewModel
import com.unionpay.demo.vm.OnlineBankViewModel

object OnlineBankFactory : IFactory {
    override fun createViewModel(context: AppCompatActivity): BaseViewModel? {
        return ViewModelProvider(
            context,
            ViewModelProvider.NewInstanceFactory()
        ).get(OnlineBankViewModel::class.java)
    }

    override fun createPayFragment(context: AppCompatActivity): BasePayFragment? {
        return OnlineBankPayFragment()
    }

    override fun createPayResultFragment(context: AppCompatActivity): BasePayResultFragment? {
        return OnlineBankPayResultFragment()
    }

    override fun getTitle() = "网银渠道-网关支付Demo"

}
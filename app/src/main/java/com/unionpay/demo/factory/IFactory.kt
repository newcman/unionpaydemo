package com.unionpay.demo.factory

import androidx.appcompat.app.AppCompatActivity
import com.unionpay.demo.ui.BasePayFragment
import com.unionpay.demo.ui.BasePayResultFragment
import com.unionpay.demo.vm.BaseViewModel

interface IFactory {
    fun createViewModel(context: AppCompatActivity): BaseViewModel?
    fun createPayFragment(context: AppCompatActivity): BasePayFragment?
    fun createPayResultFragment(context: AppCompatActivity): BasePayResultFragment?
    fun getTitle():String
}
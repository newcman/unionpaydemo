package com.unionpay.demo.factory

import com.unionpay.demo.PayActivity

object FactoryManager {
    fun getFactory(channel: Int): IFactory? {
        when (channel) {
            PayActivity.CHANNEL_ONLINE_BANK ->
                return OnlineBankFactory
        }
        return null
    }
}
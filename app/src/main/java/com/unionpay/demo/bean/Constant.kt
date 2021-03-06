package com.unionpay.demo.bean

// 常量
class Constant {
    companion object {
        const val MODEL_NORMAL = "01" // 标准模式
        const val MODEL_DIREC = "02" // 直通模式


        const val PAGE_PAY = 1
        const val PAGE_PAY_RESULT = 2
        // 商户号
        const val MERCHANT_NO_TEST = "2219020050132"
        const val MERCHANT_NO_PRODUCT = "2218120000751"

        // 环境
        const val ENV_TEST = "01"
        const val ENV_PRODUCT  = "00"

        const val RESULT_OK = "0000"

        // 支付结果
        const val PAY_SUCCESS = 1
        const val PAY_FAILED = 2
        const val PAY_CANCEL = 3



        const val WX_APP_ID = "wx9807806353070dd0"
        const val WX_SOURCE_APP_ID = "gh_9b54a64e0e1b" // 小程序原始id
    }
}
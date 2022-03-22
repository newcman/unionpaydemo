package com.unionpay.demo.util

import android.util.Log

/**
 * 支付品牌
 */
class PayBrand {
    companion object {
        private const val TAG = "PayBrand"

        /**
         * 手机品牌
         */
        // 三星
        const val PHONE_SAMSUNG = "samsung"

        // 华为
        const val PHONE_HUAWEI = "Huawei"

        // 华为荣耀
        const val PHONE_HONOR = "HONOR"

        // 魅族
        const val PHONE_MEIZU = "Meizu"

        // le
        const val PHONE_LE = "le"

        // 小米
        const val PHONE_XIAOMI = "xiaomi"

        // OPPO
        const val PHONE_OPPO = "OPPO"

        // vivo
        const val PHONE_VIVO = "vivo"

        // 锤子：
        const val PHONE_SMARTISAN = "Smartisan"

        // REALME
        const val PHONE_REALME = "htc"

        // oneplus
        const val PHONE_ONEPLUS = "OnePlus"


        /**
         * setype
         */
        // 三星
        const val SE_SAMSUNG = "02"

        // 华为
        const val SE_HUAWEI = "04"

        // 魅族
        const val SE_MEIZU = "27"

        // le
        const val SE_LE = "30"

        // 小米
        const val SE_XIAOMI = "25"

        // OPPO
        const val SE_OPPO = "29"

        // vivo
        const val SE_VIVO = "33"

        // 锤子：
        const val SE_SMARTISAN = "32"

        // REALME
        const val SE_REALME = "35"

        // oneplus
        const val SE_ONEPLUS = "36"


        fun getSeType(): String {
            val brand = android.os.Build.BRAND
            Log.d(TAG, "getSeType $brand")
            return when {
                brand.contains(PHONE_SAMSUNG, true) -> SE_SAMSUNG
                brand.contains(PHONE_HUAWEI, true) -> SE_HUAWEI
                brand.contains(PHONE_HONOR, true) -> SE_HUAWEI
                brand.contains(PHONE_MEIZU, true) -> SE_MEIZU
                brand.contains(PHONE_XIAOMI, true) -> SE_XIAOMI
                brand.contains(PHONE_OPPO, true) -> SE_OPPO
                brand.contains(PHONE_VIVO, true) -> SE_VIVO
                brand.contains(PHONE_SMARTISAN, true) -> SE_SMARTISAN
                brand.contains(PHONE_REALME, true) -> SE_REALME
                brand.contains(PHONE_ONEPLUS, true) -> SE_ONEPLUS
                brand.contains(PHONE_LE, true) -> SE_LE
                else -> ""
            }
        }
    }
}
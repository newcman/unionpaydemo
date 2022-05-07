package com.unionpay.demo

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.payeco.wallet.R
import kotlinx.android.synthetic.main.main_activity.*


/**
 * 主界面
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        initView()

        tv_version.text = "Version ${getVersionName()}"
    }

    private fun initView() {
        bt_union_pay.setOnClickListener {
            val intent = Intent(this, UnionPayActivity::class.java)
            startActivity(intent)
        }

        bt_wx_pay.setOnClickListener {
            val intent = Intent(this, WXPayActivity::class.java)
            startActivity(intent)
        }
    }

    private fun getVersionName(): String? {
        try {
            val pm: PackageManager = packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            return pi.versionName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "1.0"
    }
}
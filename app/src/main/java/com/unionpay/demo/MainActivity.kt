package com.unionpay.demo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.payeco.wallet.R
import kotlinx.android.synthetic.main.main_activity.*
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.concurrent.thread

/**
 * 主界面
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        initView()
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
}
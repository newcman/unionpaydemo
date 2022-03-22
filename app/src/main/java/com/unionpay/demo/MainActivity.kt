package com.unionpay.demo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.main_activity.*

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
package com.unionpay.uppayplugin.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.unionpay.uppayplugin.demo.ui.PayFragment

class MainActivity : AppCompatActivity() {
    private var mViewModel: PayViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        mViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(PayViewModel::class.java)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, PayFragment.newInstance())
                    .commitNow()
        }

    }
}
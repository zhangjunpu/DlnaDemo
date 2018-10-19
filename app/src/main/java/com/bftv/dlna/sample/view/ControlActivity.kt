package com.bftv.dlna.sample.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bftv.dlna.sample.R
import kotlinx.android.synthetic.main.activity_control.*

/**
 * 控制Actvity
 * @author Junpu
 * @time 2018/9/19 18:37
 */
class ControlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        initView()
    }

    private fun initView() {
        btnUrl?.setOnClickListener { }
        btnLocalImage?.setOnClickListener { }
        btnLocalVideo?.setOnClickListener { }
        btnLocalMusic?.setOnClickListener { }
    }

}
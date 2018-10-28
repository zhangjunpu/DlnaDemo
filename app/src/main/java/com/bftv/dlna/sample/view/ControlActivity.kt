package com.bftv.dlna.sample.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import com.abooc.util.Debug
import com.abooc.widget.Toast
import com.bftv.dlna.friendlyName
import com.bftv.dlna.sample.DeviceCache
import com.bftv.dlna.sample.R
import com.bftv.dlna.sample.contract.ContractControl
import com.bftv.dlna.sample.ipresenter.ControlPresenter
import com.junpu.mvp.MvpBaseActivity
import kotlinx.android.synthetic.main.activity_control.*

/**
 * 控制Actvity
 * @author Junpu
 * @time 2018/9/19 18:37
 */
class ControlActivity : MvpBaseActivity<ContractControl.ControlView, ControlPresenter>(), OnClickListener, ContractControl.ControlView {

    override fun createPresenter(): ControlPresenter = ControlPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        initActionBar()
        initView()
        presenter?.load()
    }

    private fun initActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = DeviceCache.device?.device?.friendlyName
    }

    private fun initView() {
        btnUrl?.setOnClickListener(this)
        btnLocalImage?.setOnClickListener(this)
        btnLocalVideo?.setOnClickListener(this)
        btnLocalMusic?.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v) {
            btnUrl -> {
            }
            btnLocalImage -> {
            }
            btnLocalVideo -> {
            }
            btnLocalMusic -> {
            }
        }
    }

    override fun onResult(msg: String?) {
        Debug.out("onResult: msg ---> $msg")
        Toast.show(msg)
    }

}
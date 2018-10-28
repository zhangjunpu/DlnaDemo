package com.bftv.dlna.sample.view

import android.app.AlertDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.abooc.joker.adapter.recyclerview.ViewHolder.OnRecyclerItemClickListener
import com.abooc.joker.adapter.recyclerview.ViewHolder.OnRecyclerItemLongClickListener
import com.abooc.util.Debug
import com.bftv.dlna.*
import com.bftv.dlna.callback.OnDiscoveryListener
import com.bftv.dlna.model.DeviceDisplay
import com.bftv.dlna.sample.DeviceCache
import com.bftv.dlna.sample.R
import com.bftv.dlna.sample.adapter.DeviceAdapter
import com.bftv.dlna.sample.launch
import kotlinx.android.synthetic.main.activity_main.*

/**
 *
 * @author Junpu
 * @time 2018/5/17 18:45
 */
class MainActivity : AppCompatActivity(), OnRecyclerItemClickListener, OnRecyclerItemLongClickListener {

    private val dlnaManager by lazy { DlnaManager.instance }
    private var adapter: DeviceAdapter? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initDlan()
    }

    private fun initView() {
        recyclerView?.layoutManager = LinearLayoutManager(this)
        adapter = DeviceAdapter(this)
        adapter?.setOnRecyclerItemClickListener(this)
        adapter?.setOnRecyclerItemLongClickListener(this)
        recyclerView?.adapter = adapter
    }

    private fun initDlan() {
        dlnaManager.registerDiscoveryListener(onDiscoveryListener)
        dlnaManager.startDlnaService(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_refresh -> {
                Toast.makeText(this, R.string.searching, Toast.LENGTH_SHORT).show()
                dlnaManager.removeAllRemoteDevice()
                dlnaManager.search()
            }
            R.id.action_router -> dlnaManager.switchRouter()
            R.id.action_log -> dlnaManager.switchLogger()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        dlnaManager.unregisterDiscoveryListener(onDiscoveryListener)
        dlnaManager.stopDlnaService(this)
    }

    private val onDiscoveryListener = object : OnDiscoveryListener {
        override fun onDeviceAdded(device: DeviceDisplay?) {
            val position = adapter?.getItemPosition(device) ?: 0
            Debug.out("position = $position")
            if (position >= 0) {
                adapter?.replace(position, device)
            } else {
                adapter?.add(device)
            }
            adapter?.collection?.sortBy { it.deviceIp }
        }

        override fun onDeviceRemoved(device: DeviceDisplay?) {
            adapter?.remove(device)
        }
    }

    override fun onItemClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
        val device = adapter?.getItem(position) ?: return
        DeviceCache.device = device
        launch(ControlActivity::class.java)
    }

    override fun onItemLongClick(recyclerView: RecyclerView?, itemView: View?, position: Int) {
        val device = adapter?.getItem(position) ?: return
        val sb = StringBuilder()
        sb.append("IP：${device.host}\n")
        sb.append("UDN：${device.udn}\n\n")
        sb.append(device.manufacturer)
        sb.append("\n\n")
        sb.append("DLNA服务：\n")
        device.device?.services?.forEach { sb.append(it.serviceType.type).append("\n") }

        AlertDialog.Builder(this)
                .setTitle(device.friendlyName)
                .setMessage(sb.toString())
                .setNegativeButton(R.string.OK) { _, _ -> }
                .show()
    }

}

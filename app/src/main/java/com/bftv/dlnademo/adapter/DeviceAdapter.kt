package com.bftv.dlnademo.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.abooc.joker.adapter.recyclerview.BaseRecyclerAdapter
import com.abooc.joker.adapter.recyclerview.BaseViewHolder
import com.abooc.joker.adapter.recyclerview.ViewHolder
import com.bftv.dlna.friendlyName
import com.bftv.dlna.host
import com.bftv.dlna.manufacturer
import com.bftv.dlna.model.DeviceDisplay
import com.bftv.dlnademo.R.layout
import com.bftv.dlnademo.inflate
import kotlinx.android.synthetic.main.device_item.view.*

class DeviceAdapter(context: Context) : BaseRecyclerAdapter<DeviceDisplay>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = context.inflate(layout.device_item, parent, false)
        return DeviceHolder(view, listener)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = getItem(position) ?: return
        holder as DeviceHolder
        holder.bindData(conversation)
    }

    fun getItemPosition(deviceDisplay: DeviceDisplay?): Int {
        deviceDisplay ?: -1
        return collection?.indexOf(deviceDisplay) ?: -1
    }
}

class DeviceHolder(view: View, listener: OnRecyclerItemClickListener?) : BaseViewHolder<DeviceDisplay>(view, listener) {

    override fun bindData(device: DeviceDisplay?) {
        itemView?.name?.text = device?.friendlyName
        itemView?.host?.text = device?.host
        itemView?.desc?.text = device?.manufacturer
    }
}

package com.bftv.dlna.callback

import com.bftv.dlna.model.DeviceDisplay

/**
 * 扫描设备回调
 * @author Junpu
 * @time 2018/5/17 17:35
 */
interface OnDiscoveryListener {
    fun onDeviceAdded(device: DeviceDisplay?)
    fun onDeviceRemoved(device: DeviceDisplay?)
}
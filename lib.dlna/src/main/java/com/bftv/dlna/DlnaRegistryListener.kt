package com.bftv.dlna

import android.os.Handler
import android.os.Looper
import com.abooc.util.Debug
import com.bftv.dlna.model.DeviceDisplay
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.LocalDevice
import org.fourthline.cling.model.meta.RemoteDevice
import org.fourthline.cling.registry.DefaultRegistryListener
import org.fourthline.cling.registry.Registry

/**
 * DLNA Registry Listener
 * @author Junpu
 * @time 2018/5/17 15:49
 */

class DlnaRegistryListener : DefaultRegistryListener() {

    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private val listeners = arrayListOf<OnDiscoveryListener>()

    /* Discovery performance optimization for very slow Android devices! */
    override fun remoteDeviceDiscoveryStarted(registry: Registry?, device: RemoteDevice?) {
        Debug.anchor()
        deviceAdded(device)
    }

    override fun remoteDeviceDiscoveryFailed(registry: Registry?, device: RemoteDevice?, ex: Exception?) {
        Debug.anchor()
        Debug.error("Discovery failed ...")
        Debug.printStackTrace(ex)
        deviceRemoved(device)
    }

    override fun remoteDeviceAdded(registry: Registry?, device: RemoteDevice?) {
        Debug.anchor()
        device?.log()
        deviceAdded(device)
    }

    override fun remoteDeviceRemoved(registry: Registry?, device: RemoteDevice?) {
        Debug.anchor()
        deviceRemoved(device)
    }

    override fun localDeviceAdded(registry: Registry?, device: LocalDevice?) {
        Debug.anchor()
        device?.log()
        deviceAdded(device)
    }

    override fun localDeviceRemoved(registry: Registry?, device: LocalDevice?) {
        Debug.anchor()
        deviceRemoved(device)
    }

    fun deviceAdded(device: Device<*, *, *>?) {
        Debug.out("DlnaRegistryListener.deviceAdded: ")
        logd(device?.friendlyName + ", " + device?.host)
        if (device?.isAVTransport != true) {
            Debug.out("当前设备不支持AVTransport服务...")
            return
        }
        handler.post { listeners.forEach { it.onDeviceAdded(DeviceDisplay(device)) } }
    }

    fun deviceRemoved(device: Device<*, *, *>?) {
        Debug.out("DlnaRegistryListener.deviceRemoved: ")
        logd(device?.friendlyName + ", " + device?.host)
        handler.post { listeners.forEach { it.onDeviceRemoved(DeviceDisplay(device)) } }
    }

    fun addDiscoveryListener(listener: OnDiscoveryListener?) {
        listener?.let { if (!listeners.contains(it)) listeners.add(it) }
    }

    fun removeDiscoveryListener(listener: OnDiscoveryListener?) {
        listener?.let { if (listeners.contains(it)) listeners.remove(it) }
    }

}
package com.bftv.dlna

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.abooc.util.Debug
import com.abooc.widget.Toast
import com.bftv.dlna.service.AppUpnpService
import org.fourthline.cling.android.AndroidUpnpService
import org.fourthline.cling.android.FixedAndroidLogHandler
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.transport.Router
import org.fourthline.cling.transport.RouterException
import java.util.logging.Level
import java.util.logging.Logger

/**
 *
 * @author Junpu
 * @time 2018/5/17 15:31
 */
class DlnaManager private constructor() {

    companion object {
        val instance by lazy { DlnaManager() }
    }

    private var upnpService: AndroidUpnpService? = null
    private val dlnaRegisterListener by lazy { DlnaRegistryListener() }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Debug.out("DlnaManager.onServiceConnected:  ---> DLNA服务启动")
            Debug.anchor()
            upnpService = service as? AndroidUpnpService
            upnpService?.registry?.addListener(dlnaRegisterListener)

            devices?.forEach { dlnaRegisterListener.deviceAdded(it) }
            search()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            // 走不到这，所以不放在这里
            Debug.out("DlnaManager.onServiceDisconnected: ---> DLNA服务关闭")
            upnpService = null
        }
    }

    val devices: Collection<Device<*, *, *>>?
        get() = upnpService?.registry?.devices

    private val router: Router?
        get() = upnpService?.get()?.router

    init {
        org.seamless.util.logging.LoggingUtil.resetRootHandler(FixedAndroidLogHandler())
    }

    /**
     * 开启Dlna服务
     */
    fun startDlnaService(context: Context) {
        val intent = Intent(context, AppUpnpService::class.java)
        context.applicationContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    /**
     * 关闭Dlna服务
     */
    fun stopDlnaService(context: Context) {
        upnpService?.registry?.removeListener(dlnaRegisterListener)
        context.applicationContext.unbindService(serviceConnection)
    }

    fun search() {
        upnpService?.controlPoint?.search()
//        upnpService?.controlPoint?.search(ServiceTypeHeader(UDAServiceType("AVTransport")))z
//        upnpService?.controlPoint?.search(STAllHeader())
    }

    fun removeAllRemoteDevice() {
        upnpService?.registry?.removeAllRemoteDevices()
    }

    fun registerDiscoveryListener(listener: OnDiscoveryListener?) {
        dlnaRegisterListener.addDiscoveryListener(listener)
    }

    fun unregisterDiscoveryListener(listener: OnDiscoveryListener?) {
        dlnaRegisterListener.removeDiscoveryListener(listener)
    }


    fun turnOnRouter() {
        try {
            Debug.anchor("enable")
            router?.enable()
        } catch (e: RouterException) {
            Debug.printStackTrace(e)
        }
    }

    fun turnOffRouter() {
        try {
            router?.disable()
        } catch (e: RouterException) {
            Debug.printStackTrace(e)
        }
    }

    fun switchRouter() {
        router?.let {
            try {
                if (it.isEnabled) {
                    Toast.show("关闭")
                    it.disable()
                } else {
                    Toast.show("开启")
                    it.enable()
                }
            } catch (ex: RouterException) {
                Debug.printStackTrace(ex)
            }
        }
    }

    fun setLoggerEnable(enable: Boolean) {
        val logger = Logger.getLogger("org.fourthline.cling")
        if (enable) logger?.level = Level.FINEST else logger?.level = Level.INFO
    }

    fun switchLogger() {
        val logger = Logger.getLogger("org.fourthline.cling")
        if (logger.level != null && logger.level != Level.INFO) {
            Toast.show(R.string.disable_debug_log)
            logger.level = Level.INFO
        } else {
            Toast.show(R.string.enable_debug_log)
            logger.level = Level.FINEST
        }
    }

}
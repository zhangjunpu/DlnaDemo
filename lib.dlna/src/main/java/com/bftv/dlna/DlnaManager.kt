package com.bftv.dlna

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.abooc.util.Debug
import com.abooc.widget.Toast
import com.bftv.dlna.callback.OnDiscoveryListener
import com.bftv.dlna.service.AppUpnpService
import org.fourthline.cling.android.AndroidUpnpService
import org.fourthline.cling.android.FixedAndroidLogHandler
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.transport.RouterException
import java.util.logging.Level
import java.util.logging.Logger

/**
 * DLNA管理
 * @author Junpu
 * @time 2018/5/17 15:31
 */
class DlnaManager private constructor() {

    companion object {
        val instance by lazy { DlnaManager() }
    }

    private var upnpService: AndroidUpnpService? = null
    private val dlnaRegisterListener by lazy { DlnaRegistryListener() }
    private var dlnaControlManager: DlnaControlManager? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            Debug.out("DlnaManager.onServiceConnected:  ---> DLNA服务启动")
            upnpService = service as? AndroidUpnpService
            upnpService?.registry?.addListener(dlnaRegisterListener)

            devices?.forEach { dlnaRegisterListener.deviceAdded(it) }
            search()
        }

        override fun onServiceDisconnected(className: ComponentName) {
            // 走不到这，所以注销方法不放在这里
            Debug.out("DlnaManager.onServiceDisconnected: ---> DLNA服务关闭")
            upnpService = null
        }
    }

    /**
     * 设备列表
     */
    val devices: Collection<Device<*, *, *>>?
        get() = upnpService?.registry?.devices

    init {
        org.seamless.util.logging.LoggingUtil.resetRootHandler(FixedAndroidLogHandler())
    }

    /**
     * 获取DLNAControlManager
     */
    fun getDlnaControlManager(): DlnaControlManager? {
        if (dlnaControlManager == null) {
            upnpService?.let { dlnaControlManager = DlnaControlManager(it) }
        }
        return dlnaControlManager
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

    /**
     * 搜索设备
     */
    fun search() {
        upnpService?.controlPoint?.search()
//        upnpService?.controlPoint?.search(ServiceTypeHeader(UDAServiceType("AVTransport")))
//        upnpService?.controlPoint?.search(STAllHeader())
    }

    /**
     * 清空设备列表
     */
    fun removeAllRemoteDevice() {
        upnpService?.registry?.removeAllRemoteDevices()
    }

    /**
     * 注册发现设备回调
     */
    fun registerDiscoveryListener(listener: OnDiscoveryListener?) {
        dlnaRegisterListener.addDiscoveryListener(listener)
    }

    /**
     * 注销发现设备回调
     */
    fun unregisterDiscoveryListener(listener: OnDiscoveryListener?) {
        dlnaRegisterListener.removeDiscoveryListener(listener)
    }

    /**
     * Notwork ON/OFF
     */
    fun switchRouter() {
        upnpService?.get()?.router?.let {
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

    /**
     * 是否开启DLNA log
     */
    fun setLoggerEnable(enable: Boolean) {
        val logger = Logger.getLogger("org.fourthline.cling")
        if (enable) logger?.level = Level.FINEST else logger?.level = Level.INFO
    }

    /**
     * log ON/OFF
     */
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
@file:JvmName("DlnaExtends")

package com.bftv.dlna

import android.util.Log
import com.bftv.dlna.model.DeviceDisplay
import org.fourthline.cling.model.meta.Device
import org.fourthline.cling.model.meta.RemoteDeviceIdentity
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.model.types.UDAServiceType
import org.fourthline.cling.model.types.UDN
import org.fourthline.cling.support.model.TransportInfo
import org.fourthline.cling.support.model.TransportState
import java.net.URI
import java.net.URL

/**
 * DLNA设备相关
 * @author Junpu
 * @time 2018/5/17 16:32
 */

val Device<*, *, *>.friendlyName: String?
    get() = details?.friendlyName

val Device<*, *, *>.descriptorURL: URL?
    get() = (identity as? RemoteDeviceIdentity)?.descriptorURL

val Device<*, *, *>.host: String?
    get() = descriptorURL?.host

val Device<*, *, *>.port: Int
    get() = descriptorURL?.port ?: -1

val Device<*, *, *>.manufacturer: String?
    get() = details?.manufacturerDetails?.manufacturer

val Device<*, *, *>.manufacturerURL: URI?
    get() = details?.manufacturerDetails?.manufacturerURI

val Device<*, *, *>.modelName: String?
    get() = details?.modelDetails?.modelName

val Device<*, *, *>.modelDescription: String?
    get() = details?.modelDetails?.modelDescription

val Device<*, *, *>.modelNumber: String?
    get() = details?.modelDetails?.modelNumber

val Device<*, *, *>.modelURI: URI?
    get() = details?.modelDetails?.modelURI

val Device<*, *, *>.baseURL: URL?
    get() = details?.baseURL

val Device<*, *, *>.presentationURI: URI?
    get() = details?.presentationURI

val Device<*, *, *>.serialNumber: String?
    get() = details?.serialNumber

val Device<*, *, *>.udn: UDN?
    get() = identity?.udn

val Device<*, *, *>.uuid: String?
    get() = identity?.udn?.identifierString

val Device<*, *, *>.avTransportService: Service<*, *>?
    get() = findService(UDAServiceType("AVTransport"))

val Device<*, *, *>.renderingControlService: Service<*, *>?
    get() = findService(UDAServiceType("RenderingControl"))

fun Device<*, *, *>.log() {
    logd("device info : ====================================================================>")
    logd("friendlyName ---> $friendlyName")
    logd("host ---> $host")
    logd("port ---> $port")
    logd("descriptorURL ---> $descriptorURL")
    logd("uuid ---> $uuid")
    logd("manufacturer ---> $manufacturer")
    logd("modelName ---> $modelName")
    logd("modelDescription ---> $modelDescription")
    logd("modelNumber ---> $modelNumber")
    logd("hasServices ---> ${hasServices()}")
    logd("---------------------------------------->")
    services.forEach { logd("serviceType ---> ${it.serviceType}") }
    logd("---------------------------------------->")
    logd("type ---> $type")
    logd("icons ---> ${icons.size}")
    logd("===========================================================================>")
}


val DeviceDisplay.displayString: String?
    get() = device?.displayString

val DeviceDisplay.friendlyName: String?
    get() = device?.friendlyName

val DeviceDisplay.descriptorURL: String?
    get() = device?.descriptorURL.toString()

val DeviceDisplay.host: String?
    get() = device?.host

val DeviceDisplay.port: Int
    get() = device?.port ?: -1

val DeviceDisplay.manufacturer: String?
    get() = device?.manufacturer

val DeviceDisplay.modelName: String?
    get() = device?.modelName

val DeviceDisplay.modelDescription: String?
    get() = device?.modelDescription

val DeviceDisplay.udn: String?
    get() = device?.udn.toString()

val DeviceDisplay.uuid: String?
    get() = device?.udn?.identifierString

val DeviceDisplay.isFullyHydrated: Boolean
    get() = device?.isFullyHydrated == true

val DeviceDisplay.avTransportService: Service<*, *>?
    get() = device?.avTransportService

val DeviceDisplay.renderingControlService: Service<*, *>?
    get() = device?.renderingControlService

/**
 * 快速获取ip尾数
 */
val DeviceDisplay.deviceIp: Int
    get() {
        if (host.isNullOrEmpty()) return 0
        val start = host?.lastIndexOf(".") ?: -1
        return host?.substring(start + 1)?.toInt() ?: 0
    }

fun DeviceDisplay.log() {
    device?.log()
}

fun logd(msg: String?) {
    Log.d("Upnp", msg)
}


val TransportInfo.isPlaying: Boolean
    get() = currentTransportState == TransportState.PLAYING

val TransportInfo.isPause: Boolean
    get() = currentTransportState == TransportState.PAUSED_PLAYBACK

val TransportInfo.isStop: Boolean
    get() = currentTransportState == TransportState.STOPPED

val TransportInfo.isNoMediaPresent: Boolean
    get() = currentTransportState == TransportState.NO_MEDIA_PRESENT

val TransportInfo.isTransitioning: Boolean
    get() = currentTransportState == TransportState.TRANSITIONING


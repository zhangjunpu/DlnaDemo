package com.bftv.dlna.model

import com.bftv.dlna.friendlyName
import com.bftv.dlna.host
import com.bftv.dlna.manufacturer
import org.fourthline.cling.model.meta.Device

/**
 * @author Junpu
 * @time 2018/3/8 16:04
 */
class DeviceDisplay(var device: Device<*, *, *>?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as DeviceDisplay
        if (device != other.device) return false
        return true
    }

    override fun hashCode(): Int {
        return device?.hashCode() ?: 0
    }

    override fun toString(): String {
        return "$friendlyName\n$host\n$manufacturer"
    }

}

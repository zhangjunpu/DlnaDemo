package com.bftv.dlna

import com.bftv.dlna.model.DeviceDisplay

/**
 * AVTransport服务、RenderingControl服务
 * @author Junpu
 * @time 2018/9/11 16:04
 */
interface IDlnaControl {
    fun bind(device: DeviceDisplay?)
    fun unbind()
    fun getVolume()
    fun setVolume(volume: Long)
    fun getMute()
    fun setMute(mute: Boolean)
    fun setUrl(uri: String?, metadata: String?)
    fun play()
    fun pause()
    fun stop()
    fun seek(progress: String?)
    fun getPositionInfo()
    fun getMediaInfo()
    fun getTransportInfo()
}

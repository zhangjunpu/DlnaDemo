package com.bftv.dlna.callback

import org.fourthline.cling.support.model.MediaInfo
import org.fourthline.cling.support.model.TransportState

/**
 * DLNA控制回调
 * @author Junpu
 * @time 2018/9/11 16:11
 */
interface OnDlnaControlListener {
    fun onNoMediaPresent()
    fun onPlaying()
    fun onPaused()
    fun onStop()
    fun onPrepare()
    fun onPlayEnd()
    fun onSeeking()
    fun onVolumeChanged(volume: Long)
    fun onMuteChanged(mute: Boolean)
    fun onStateChanged(state: TransportState?)
    fun onProgressChanged(position: Long, duration: Long)
    fun onMediaInfo(mediaInfo: MediaInfo?)
}

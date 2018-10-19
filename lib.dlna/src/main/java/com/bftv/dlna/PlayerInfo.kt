package com.bftv.dlna

import org.fourthline.cling.support.model.MediaInfo
import org.fourthline.cling.support.model.PositionInfo
import org.fourthline.cling.support.model.TransportInfo
import org.fourthline.cling.support.model.TransportState

/**
 * 负责记录远端媒体播放状态、媒体信息、播放进度。
 * @author Junpu
 * @time 2018/9/11 16:14
 */
class PlayerInfo internal constructor() {

    var volume: Long = 0
    var mute = false

    // Track info
    var positionInfo: PositionInfo? = PositionInfo()
    var mediaInfo: MediaInfo? = MediaInfo()
    var transportInfo: TransportInfo? = TransportInfo()

    fun updateSeek(progress: String?) {
        positionInfo?.relTime = progress
    }

    /**
     * 更新状态
     */
    fun update(state: TransportState?) {
        transportInfo = TransportInfo(state)
    }

    fun stop() {
        transportInfo = TransportInfo(TransportState.STOPPED)
    }

    fun clear() {
        positionInfo = PositionInfo()
        mediaInfo = MediaInfo()
        transportInfo = TransportInfo()
    }
}

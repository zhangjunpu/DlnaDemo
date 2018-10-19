package com.bftv.dlna

import com.abooc.util.Debug

/**
 * 持久心跳，不断获取远端播放的最新状态，包括：播放状态、在播媒体信息
 * @author Junpu
 * @time 2018/9/12 14:39
 */
class DlnaControlThread(private val manager: DlnaControlManager?): Runnable {

    private var iThread: Thread? = null
    private var isRunning: Boolean = false

    /**
     * 开始
     */
    fun start() {
        Debug.anchor()
        isRunning = true
        iThread = Thread(this)
        iThread?.start()
    }

    /**
     * 停止
     */
    fun stop() {
        Debug.anchor()
        isRunning = false
        iThread?.interrupt()
        iThread = null
    }

    override fun run() {
        Debug.anchor()
        try {
            var count = 0
            while (isRunning) {
                count++
                if (manager?.playerInfo?.transportInfo?.isPlaying == true) manager.getPositionInfo() // 每秒获取一次远端进度信息
                manager?.getTransportInfo() // 每秒获取一次远端状态
                if (count % 6 == 0) {
                    count = 0
                    manager?.getMediaInfo() // 每6秒获取1次远端媒体信息
                }
                Thread.sleep(1000)
            }
        } catch (e: InterruptedException) {
            Debug.printStackTrace(e)
        }
    }

}
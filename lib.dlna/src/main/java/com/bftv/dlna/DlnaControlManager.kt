package com.bftv.dlna

import com.abooc.util.Debug
import com.bftv.dlna.callback.OnActionListener
import com.bftv.dlna.callback.OnDlnaControlListener
import com.bftv.dlna.model.DeviceDisplay
import org.fourthline.cling.android.AndroidUpnpService
import org.fourthline.cling.controlpoint.ActionCallback
import org.fourthline.cling.controlpoint.ControlPoint
import org.fourthline.cling.controlpoint.SubscriptionCallback
import org.fourthline.cling.model.action.ActionInvocation
import org.fourthline.cling.model.gena.CancelReason
import org.fourthline.cling.model.gena.GENASubscription
import org.fourthline.cling.model.message.UpnpResponse
import org.fourthline.cling.model.meta.Service
import org.fourthline.cling.registry.Registry
import org.fourthline.cling.support.avtransport.callback.*
import org.fourthline.cling.support.model.MediaInfo
import org.fourthline.cling.support.model.PositionInfo
import org.fourthline.cling.support.model.TransportInfo
import org.fourthline.cling.support.model.TransportState
import org.fourthline.cling.support.renderingcontrol.callback.GetMute
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume
import org.fourthline.cling.support.renderingcontrol.callback.SetMute
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume

/**
 * DLNA控制服务管理
 * @author Junpu
 * @time 2018/9/11 15:54
 */
class DlnaControlManager(upnpService: AndroidUpnpService?) : IDlnaControl {

    var playerInfo: PlayerInfo? = null
    var isSending: Boolean = false
    var isBind = false

    private var controlPoint: ControlPoint? = null
    private var registry: Registry? = null
    private var device: DeviceDisplay? = null
    private var onActionListener: OnActionListener? = null
    private var onDlnaControlListener: OnDlnaControlListener? = null
    private val controlThread by lazy { DlnaControlThread(this) }
    private var hasCallEnd = false
    private var bindSubscriptionId: String? = null

    init {
        controlPoint = upnpService?.controlPoint
        registry = upnpService?.registry
        playerInfo = PlayerInfo()
    }

    fun setOnActionListener(listener: OnActionListener?) {
        onActionListener = listener
    }

    fun addOnRenderListener(listener: OnDlnaControlListener?) {
        onDlnaControlListener = listener
    }

    /**
     * 绑定设备
     */
    override fun bind(device: DeviceDisplay?) {
        this.device = device
        controlPoint?.execute(object : SubscriptionCallback(device?.avTransportService) {
            override fun established(subscription: GENASubscription<out Service<*, *>>?) {
                Debug.out("DlnaControlManager.established: ")
                bindSubscriptionId = subscription?.subscriptionId
                isBind = true
            }

            override fun eventReceived(subscription: GENASubscription<out Service<*, *>>?) {
                Debug.out("DlnaControlManager.eventReceived: ")
            }

            override fun ended(subscription: GENASubscription<out Service<*, *>>?, reason: CancelReason?, responseStatus: UpnpResponse?) {
                Debug.out("DlnaControlManager.ended: ")
                isBind = false
            }

            override fun eventsMissed(subscription: GENASubscription<out Service<*, *>>?, numberOfMissedEvents: Int) {
                Debug.out("DlnaControlManager.eventsMissed: ")
            }

            override fun failed(subscription: GENASubscription<out Service<*, *>>?, responseStatus: UpnpResponse?, exception: Exception?, defaultMsg: String?) {
                Debug.out("DlnaControlManager.failed: ")
                isBind = false
            }
        })
    }

    /**
     * 取消绑定
     */
    override fun unbind() {
        if (isBind) {
            isBind = false
            registry?.getRemoteSubscription(bindSubscriptionId)?.let { registry?.removeRemoteSubscription(it) }
            bindSubscriptionId = null
            device = null
        }
    }

    /**
     * 执行命令
     */
    private fun execute(actionCallback: ActionCallback?) {
        Debug.out("execute: actionCallback ---> ${actionCallback?.javaClass?.simpleName}")
        isSending = true
        onActionListener?.onSend()
        controlPoint?.execute(actionCallback)
    }

    private fun sendFinish(success: Boolean) {
        isSending = false
        onActionListener?.onSendFinish(success)
    }

    /**
     * 获取音量
     */
    override fun getVolume() {
        device?.renderingControlService ?: return
        execute(object : GetVolume(device?.renderingControlService) {
            override fun received(invocation: ActionInvocation<out Service<*, *>>?, currentVolume: Int) {
                playerInfo?.volume = currentVolume.toLong()
                sendFinish(true)
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
                sendFinish(false)
            }
        })
    }

    /**
     * 设置音量
     */
    override fun setVolume(volume: Long) {
        device?.renderingControlService ?: return
        execute(object : SetVolume(device?.renderingControlService, volume) {
            override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                onDlnaControlListener?.onVolumeChanged(volume)
                playerInfo?.volume = volume
                sendFinish(true)
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
                sendFinish(false)
            }
        })
    }

    /**
     * 获取静音状态
     */
    override fun getMute() {
        device?.renderingControlService ?: return
        execute(object : GetMute(device?.renderingControlService) {
            override fun received(invocation: ActionInvocation<out Service<*, *>>?, mute: Boolean) {
                playerInfo?.mute = mute
                sendFinish(true)
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
                sendFinish(false)
            }
        })
    }

    /**
     * 设置静音状态
     */
    override fun setMute(mute: Boolean) {
        device?.renderingControlService ?: return
        execute(object : SetMute(device?.renderingControlService, mute) {
            override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                onDlnaControlListener?.onMuteChanged(mute)
                playerInfo?.mute = mute
                sendFinish(true)
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
                sendFinish(false)
            }
        })
    }

    /**
     * 设置播放Url
     */
    override fun setUrl(uri: String?, metadata: String?) {
        device?.avTransportService ?: return
        Debug.anchor("$uri\n metadata:$metadata")
        execute(object : SetAVTransportURI(device?.avTransportService, uri, metadata) {
            override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                play()
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
                sendFinish(false)
            }
        })
    }

    /**
     * 播放
     */
    override fun play() {
        device?.avTransportService ?: return
        execute(object : Play(device?.avTransportService) {
            override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                onDlnaControlListener?.onPlaying()
                onDlnaControlListener?.onStateChanged(TransportState.PLAYING)
                playerInfo?.update(TransportState.PLAYING)
                sendFinish(true)
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
                Debug.error(invocation)
                sendFinish(false)
            }
        })
    }

    /**
     * 暂停
     */
    override fun pause() {
        device?.avTransportService ?: return
        execute(object : Pause(device?.avTransportService) {
            override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                onDlnaControlListener?.onPaused()
                onDlnaControlListener?.onStateChanged(TransportState.PAUSED_PLAYBACK)
                playerInfo?.update(TransportState.PAUSED_PLAYBACK)
                sendFinish(true)
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
                sendFinish(false)
            }
        })
    }

    /**
     * 停止
     */
    override fun stop() {
        device?.avTransportService ?: return
        execute(object : Stop(device?.avTransportService) {
            override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                onDlnaControlListener?.onStop()
                onDlnaControlListener?.onStateChanged(TransportState.STOPPED)
                playerInfo?.update(TransportState.STOPPED)
                sendFinish(true)
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
                sendFinish(false)
            }
        })
    }

    /**
     * 调节播放进度
     * @param progress 目标时间，格式HH:mm:ss
     */
    override fun seek(progress: String?) {
        device?.avTransportService ?: return
        execute(object : Seek(device?.avTransportService, progress) {
            override fun success(invocation: ActionInvocation<out Service<*, *>>?) {
                playerInfo?.updateSeek(progress)
                sendFinish(true)
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
                sendFinish(false)
            }
        })
    }

    /**
     * 获取远端进度
     */
    override fun getPositionInfo() {
        device?.avTransportService ?: return
        execute(object : GetPositionInfo(device?.avTransportService) {
            override fun received(invocation: ActionInvocation<out Service<*, *>>?, positionInfo: PositionInfo?) {
                positionInfo ?: return
                val duration = positionInfo.trackDurationSeconds
                val progress = positionInfo.trackElapsedSeconds
                onDlnaControlListener?.onProgressChanged(progress, duration)
                // 如果当前进度与总时长相差小雨2秒，则停止
                if (duration > 0 && duration >= progress && duration - progress <= 2) {
                    if (!hasCallEnd) {
                        hasCallEnd = true
                        onDlnaControlListener?.onPlayEnd()
                    }
                } else {
                    hasCallEnd = false
                }
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {
                sendFinish(false)
            }
        })
    }

    /**
     * 获取远端播放信息
     */
    override fun getMediaInfo() {
        device?.avTransportService ?: return
        execute(object : GetMediaInfo(device?.avTransportService) {
            override fun received(invocation: ActionInvocation<out Service<*, *>>?, mediaInfo: MediaInfo?) {
                onDlnaControlListener?.onMediaInfo(mediaInfo)
                playerInfo?.mediaInfo = mediaInfo
                sendFinish(true)
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {}
        })
    }

    /**
     * 获取远端播放状态
     */
    override fun getTransportInfo() {
        device?.avTransportService ?: return
        execute(object : GetTransportInfo(device?.avTransportService) {
            override fun received(invocation: ActionInvocation<out Service<*, *>>?, transportInfo: TransportInfo?) {
                val transportState = transportInfo?.currentTransportState
                onDlnaControlListener?.onStateChanged(transportState)
                handState(transportState)
                playerInfo?.update(transportState)
                playerInfo?.transportInfo = transportInfo
                sendFinish(true)
            }

            override fun failure(invocation: ActionInvocation<out Service<*, *>>?, operation: UpnpResponse?, defaultMsg: String?) {}
        })
    }

    private fun handState(state: TransportState?) {
        Debug.anchor(state)
        when (state) {
            TransportState.NO_MEDIA_PRESENT -> onDlnaControlListener?.onNoMediaPresent()
            TransportState.RECORDING -> onDlnaControlListener?.onPrepare()
            TransportState.PLAYING -> onDlnaControlListener?.onPlaying()
            TransportState.PAUSED_PLAYBACK -> onDlnaControlListener?.onPaused()
            TransportState.STOPPED -> onDlnaControlListener?.onStop()
            TransportState.TRANSITIONING -> onDlnaControlListener?.onSeeking()
            else -> Unit
        }
    }


    /**
     * 开始
     */
    fun startThread() {
        controlThread.start()
    }

    /**
     * 停止
     */
    fun stopThread() {
        controlThread.stop()
    }

}

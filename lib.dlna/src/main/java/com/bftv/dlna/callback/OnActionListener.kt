package com.bftv.dlna.callback

/**
 * DLNA指令回调
 * @author Junpu
 * @time 2018/9/10 18:39
 */
interface OnActionListener {
    fun onSend()
    fun onSendFinish(success: Boolean)
}
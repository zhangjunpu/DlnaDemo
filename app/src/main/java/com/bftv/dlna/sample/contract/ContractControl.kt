package com.bftv.dlna.sample.contract

import com.junpu.mvp.MvpView

/**
 *
 * @author Junpu
 * @time 2018/10/25 16:57
 */
interface ContractControl {

    interface ControlView : MvpView {
        fun onResult(msg: String?)
    }

    interface ControlModel {
        fun request(callBack: IControlCallBack)
    }

    interface IControlCallBack {
        fun onResult(msg: String?)
    }
}
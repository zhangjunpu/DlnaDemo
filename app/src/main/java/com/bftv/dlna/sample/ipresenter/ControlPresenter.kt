package com.bftv.dlna.sample.ipresenter

import com.abooc.util.Debug
import com.bftv.dlna.DlnaControlManager
import com.bftv.dlna.DlnaManager
import com.bftv.dlna.sample.contract.ContractControl
import com.bftv.dlna.sample.contract.impl.ControlModelImpl
import com.junpu.mvp.MvpBasePresenter

/**
 *
 * @author Junpu
 * @time 2018/10/25 16:37
 */
class ControlPresenter: MvpBasePresenter<ContractControl.ControlView>() {

    private var contractControl: ControlModelImpl? = null

    private var dlnaManager: DlnaManager? = null
    private var dlnaControlManager: DlnaControlManager? = null

    override fun onStart() {
        super.onStart()
        Debug.out("ControlPresenter.onStart: ")
        contractControl = ControlModelImpl()
        dlnaManager = DlnaManager.instance
        dlnaControlManager = dlnaManager?.getDlnaControlManager()
    }

    fun load() {
        contractControl?.request(object :ContractControl.IControlCallBack {
            override fun onResult(msg: String?) {
                view?.onResult(msg)
            }
        })
    }
}
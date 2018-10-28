package com.bftv.dlna.sample.contract.impl

import com.bftv.dlna.sample.contract.ContractControl.ControlModel
import com.bftv.dlna.sample.contract.ContractControl.IControlCallBack

/**
 *
 * @author Junpu
 * @time 2018/10/25 17:04
 */
class ControlModelImpl: ControlModel {

    /**
     * 请求数据
     */
    override fun request(callBack: IControlCallBack) {
        callBack.onResult("request data...")
    }
}
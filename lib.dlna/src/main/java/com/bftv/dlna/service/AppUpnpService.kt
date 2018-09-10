package com.bftv.dlna.service

import org.fourthline.cling.UpnpServiceConfiguration
import org.fourthline.cling.android.AndroidUpnpServiceConfiguration
import org.fourthline.cling.android.AndroidUpnpServiceImpl
import org.fourthline.cling.model.types.ServiceType

class AppUpnpService : AndroidUpnpServiceImpl() {

    override fun createConfiguration(): UpnpServiceConfiguration {
        return object : AndroidUpnpServiceConfiguration() {
//            override fun getRegistryMaintenanceIntervalMillis(): Int {
//                // 维护时间
//                return 7000
//            }
//
//            override fun getDescriptorRetrievalHeaders(identity: RemoteDeviceIdentity?): UpnpHeaders? {
//                if (identity?.udn?.identifierString == UDN_STRING) {
//                    val headers = UpnpHeaders()
//                    headers.add(UpnpHeader.Type.USER_AGENT.httpName, "MyCustom/Agent")
//                    headers.add("X-Custom-Header", "foo")
//                    return headers
//                }
//                return null
//            }
//
//            override fun getEventSubscriptionHeaders(service: RemoteService?): UpnpHeaders? {
//                if (service?.serviceType?.implementsVersion(UDAServiceType("Foo", 1)) == true) {
//                    val headers = UpnpHeaders()
//                    headers.add("X-Custom-Header", "bar")
//                    return headers
//                }
//                return null
//            }

            override fun getExclusiveServiceTypes(): Array<ServiceType> {
                // 过滤要搜索的服务类型
//                return arrayOf(UDAServiceType("RenderingControl"), UDAServiceType("AVTransport"))
                return arrayOf()
            }
        }
    }

}
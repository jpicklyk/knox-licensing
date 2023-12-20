package net.sfelabs.knox_tactical

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager

object KnoxTacticalExtensions {
    fun SystemManager.configureDhcpEthernetInterface(
        interfaceName: String
    ): Boolean {
        return setEthernetConfigurations(
            interfaceName,
            CustomDeviceManager.ETHERNET_DHCP,
            null,
            null,
            null,
            null
        )

    }

    fun SystemManager.configureStaticEthernetInterface(
        interfaceName: String,
        ipAddress: String,
        dnsList: List<String> = emptyList(),
        defaultRouter: String? = null,
        netmask: String
    ): Boolean {
        return setEthernetConfigurationsMultiDns(
            interfaceName,
            CustomDeviceManager.ETHERNET_STATIC_IP,
            ipAddress,
            dnsList,
            defaultRouter,
            netmask
        )
    }

    @TestingVisibilityOnly
    fun SystemManager.testSetEthernetConfigurations(
        interfaceName: String,
        ipAddress: String,
        netmask: String,
        dnsAddress: String? = null,
        defaultRouter: String? = null
    ): Boolean {
        return setEthernetConfigurations(
            interfaceName,
            CustomDeviceManager.ETHERNET_STATIC_IP,
            ipAddress,
            dnsAddress,
            defaultRouter,
            netmask
        )
    }

    @TestingVisibilityOnly
    fun SystemManager.testSetEthernetConfigurationsMultiDns(
        interfaceName: String,
        ipAddress: String,
        netmask: String,
        dnsList: List<String>? = null,
        defaultRouter: String? = null
    ): Boolean {
        return setEthernetConfigurationsMultiDns(
            interfaceName,
            CustomDeviceManager.ETHERNET_STATIC_IP,
            ipAddress,
            dnsList,
            defaultRouter,
            netmask
        )
    }

}
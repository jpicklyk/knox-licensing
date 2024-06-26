package net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model

import java.net.Inet4Address
import java.net.InterfaceAddress

data class InterfaceConfiguration(
    val interfaceType: InterfaceType = InterfaceType.Unknown,
    val configurationName: String ="",
    val interfaceName: String = "",
    val macAddress: String? = null,
    val ipAddresses: List<InterfaceAddressConfiguration> = emptyList(),
    val isInterfaceMonitored: Boolean = false
)

fun InterfaceConfiguration.addressList(): List<String> {
    return ipAddresses.map { ip ->
        val prefixLength = netmaskToPrefix(ip.netmask)
        "${ip.ipAddress}/${prefixLength}"
    }
}

fun InterfaceConfiguration.isConfiguredCorrectly(networkState: Interface): Boolean {
    //TODO - Compare actual network to configuration
    return false
}

fun InterfaceConfiguration.updatedIpv4AddressList(inetAddresses: List<InterfaceAddress>)
: List<InterfaceAddressConfiguration> {
    val ipv4Addresses = inetAddresses.filter { it.address is Inet4Address }
    // Use zip to iterate over both lists simultaneously
    return ipAddresses.zip(ipv4Addresses) { addressConfig, inetAddress ->
        if (addressConfig.type == InterfaceAddressConfigurationType.DHCP) {
            addressConfig.copy(
                ipAddress = inetAddress.address.hostAddress!!,
                netmask = prefixToNetmask(inetAddress.networkPrefixLength),
                broadcast = inetAddress.broadcast.hostAddress
            )
        } else {
            addressConfig
        }
    }
}


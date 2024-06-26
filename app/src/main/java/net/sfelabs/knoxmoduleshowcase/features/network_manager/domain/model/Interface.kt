package net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model

import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InterfaceAddress
import java.net.NetworkInterface
import java.net.SocketException


data class Interface(
    val name: String,
    val interfaceAddresses: List<InterfaceAddress> = emptyList(),
    val ipAddresses: List<String> = inetAddressesToStrings(interfaceAddresses),
    val macAddress: String? = null,
    val networkStatus: NetworkInterfaceState = NetworkInterfaceState.Unknown,
    val isUp: Boolean = false
)

fun inetAddressesToStrings(interfaceAddresses: List<InterfaceAddress>): List<String> =
    interfaceAddresses.mapNotNull { it.address?.hostAddress }
        .map {
            if (it.contains(':')) { // Check if it's an IPv6 address
                it.substringBefore('%') // Cut off characters after '%'
            } else {
                it // Keep IPv4 addresses as they are
            }
        }
        .sortedBy { it.contains(':') }

fun Interface.ipv4Addresses(): List<InterfaceAddress> {
    return interfaceAddresses.filter {it.address is Inet4Address}

}

fun Interface.ipv6Addresses(): List<InterfaceAddress> {
    return interfaceAddresses.filter {it.address is Inet6Address}
}


fun NetworkInterface.toInterface(isConnected: Boolean? = null): Interface {
    val macAddress = try {
        hardwareAddress?.let { it.joinToString(":") { byte -> "%02x".format(byte) } }
    } catch (e: SocketException) {
        null
    }
    return Interface(
        name = name,
        interfaceAddresses = interfaceAddresses,
        macAddress = macAddress,
        networkStatus = when (isConnected) {
            true -> NetworkInterfaceState.Connected
            false -> NetworkInterfaceState.Disconnected
            null -> NetworkInterfaceState.Unknown
        },
        isUp = isUp
    )
}

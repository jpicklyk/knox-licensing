package net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model

import java.net.InterfaceAddress

data class InterfaceAddressConfiguration(
    val index: Int,
    val type: InterfaceAddressConfigurationType = InterfaceAddressConfigurationType.DHCP,
    val ipAddress: String,
    val netmask: String = "255.255.255.0",
    val broadcast: String? = null,
    val gateway: String? = null,
    val dnsList: List<String> = emptyList()
)

fun inetAddressesToConfiguration(interfaceAddresses: List<InterfaceAddress>): List<InterfaceAddressConfiguration> =
    interfaceAddresses.mapIndexed { index, interfaceAddress ->
        InterfaceAddressConfiguration(
        index = index,
        ipAddress = interfaceAddress.address?.hostAddress.orEmpty(),
        netmask = prefixToNetmask(interfaceAddress.networkPrefixLength.toInt())
    )}

fun prefixToNetmask(prefixLength: Short): String {
    return prefixToNetmask(prefixLength.toInt())
}

fun prefixToNetmask(prefixLength: Int): String {
    require(prefixLength in 0..32) { "Prefix must be between 0 and 32" }

    val binaryNetmask = "1".repeat(prefixLength) + "0".repeat(32 - prefixLength)
    val octets = mutableListOf<Int>()
    for (i in 0 until 4) {
        val octetBinary = binaryNetmask.substring(i * 8, (i + 1) * 8)
        octets.add(Integer.parseInt(octetBinary, 2))
    }
    return octets.joinToString(".")
}

fun netmaskToPrefix(netmask: String): Int {
    require(netmask.matches(Regex("\\d+\\.\\d+\\.\\d+\\.\\d+"))) { "Invalid netmask format" }

    val octets = netmask.split(".")
    val binaryNetmask = octets.joinToString("") {
        Integer.toBinaryString(it.toInt()).padStart(8, '0')
    }
    return binaryNetmask.count { it == '1' }
}
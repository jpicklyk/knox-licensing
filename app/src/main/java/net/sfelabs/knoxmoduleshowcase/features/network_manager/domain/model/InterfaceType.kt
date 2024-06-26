package net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model

sealed class InterfaceType(val value: String, val namePrefix: String) {
    data object Ethernet: InterfaceType(value = "Ethernet", namePrefix = "eth")
    data object Wifi: InterfaceType(value = "Wi-Fi", namePrefix = "wlan")
    data object Rndis: InterfaceType(value = "RNDIS", namePrefix = "rndis")
    data object ReverseTether: InterfaceType(value = "Reverse Tether", namePrefix = "rndis")
    data object Bridge: InterfaceType(value = "Bridge", namePrefix = "br")
    data object IpipTunnel: InterfaceType(value = "IPIP Tunnel", namePrefix = "ipip")
    data object Unknown: InterfaceType(value = "Unknown", namePrefix = "")

    companion object {
        val allTypes: List<InterfaceType> get() =
            listOf(Unknown, Ethernet, Rndis, ReverseTether, Bridge, IpipTunnel)
    }

    fun Interface.toInterfaceType(): InterfaceType {
        val name = name
        return when {
            name.startsWith("eth") -> Ethernet
            name.startsWith("br") -> Bridge
            name.startsWith("ipip") -> IpipTunnel
            name.startsWith("rndis") -> Rndis
            name.startsWith("wlan") -> Wifi
            else -> Unknown
        }
    }
}

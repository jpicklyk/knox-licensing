package net.sfelabs.knox_tactical.domain.model

sealed interface EthernetInterface {
    val name: String
    val type: EthernetInterfaceType
}

data class DhcpEthernetInterface(
    override val name: String
): EthernetInterface {
    override val type: EthernetInterfaceType
        get() = EthernetInterfaceType.DHCP
}

data class StaticEthernetInterface(
    override val name: String,
    val ipAddress: String,
    val gateway: String? = null,
    val netmask: String,
    val dnsList: List<String> = emptyList()
): EthernetInterface {
    override val type: EthernetInterfaceType
        get() = EthernetInterfaceType.STATIC
}
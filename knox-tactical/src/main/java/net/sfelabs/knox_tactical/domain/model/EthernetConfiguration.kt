package net.sfelabs.knox_tactical.domain.model

sealed interface EthernetConfiguration {
    val name: String
    val type: EthernetInterfaceType
}

data class DhcpConfiguration(
    override val name: String
): EthernetConfiguration {
    override val type: EthernetInterfaceType
        get() = EthernetInterfaceType.DHCP
}

data class StaticConfiguration(
    override val name: String,
    val ipAddress: String,
    val gateway: String? = null,
    val netmask: String,
    val dnsList: List<String> = emptyList()
): EthernetConfiguration {
    override val type: EthernetInterfaceType
        get() = EthernetInterfaceType.STATIC
}
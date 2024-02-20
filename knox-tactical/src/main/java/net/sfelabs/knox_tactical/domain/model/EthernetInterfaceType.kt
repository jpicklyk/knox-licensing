package net.sfelabs.knox_tactical.domain.model

sealed class EthernetInterfaceType(val interfaceType: String) {
    companion object {
        operator fun invoke(type: String): EthernetInterfaceType {
            return if (type.equals("DHCP", true)) DHCP else STATIC
        }
    }
    data object DHCP: EthernetInterfaceType("DHCP")
    data object STATIC: EthernetInterfaceType("Static")
}

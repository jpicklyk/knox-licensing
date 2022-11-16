package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.presentation

import net.sfelabs.knox_tactical.domain.model.AutoConnectionState
import net.sfelabs.knox_tactical.domain.model.EthernetInterfaceType

data class EthernetConfigurationState (
    val isLoading: Boolean = false,
    val autoConnectionState: AutoConnectionState? = null,
    val interfaceType: EthernetInterfaceType = EthernetInterfaceType.DHCP,
    val interfaceName: String = "eth0",
    val ipAddress: String? = null,
    val gateway: String? = null,
    val netmask: String? = null,
    val dnsList: String = ""
    ) {
    fun isAutoConnectionEnabled():Boolean {
        return autoConnectionState == AutoConnectionState.ON
    }
}
package net.sfelabs.knoxmoduleshowcase.features.network_manager

import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.Interface
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceAddressConfiguration
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceConfiguration

data class NetworkManagerState(
    val interfaceMap: Map<String, Interface> = emptyMap(),
    val interfaces: List<InterfaceConfiguration> = emptyList(),
    val selectedInterface: Interface? = null,
    val selectedInterfaceConfiguration: InterfaceConfiguration? = null,
    val selectedInterfaceAddressConfiguration: InterfaceAddressConfiguration? = null
) {
}
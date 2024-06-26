package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model

data class NetworkUpdate(
    val interfaceName: String?,
    val ipAddress: String?,
    val handle: Long,
    val isConnected: NetworkInterfaceState
)

package net.sfelabs.common.core.model

data class NetworkUpdate(
    val interfaceName: String?,
    val ipAddress: String?,
    val handle: Long,
    val isConnected: NetworkStatus
)

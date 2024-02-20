package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model

sealed class NetworkStatus {
    data object Unknown: NetworkStatus()
    data object Connected: NetworkStatus()
    data object Disconnected: NetworkStatus()
}
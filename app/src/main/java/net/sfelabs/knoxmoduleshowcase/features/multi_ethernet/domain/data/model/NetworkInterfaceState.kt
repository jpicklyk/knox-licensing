package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model

sealed class NetworkInterfaceState {
    data object Unknown: NetworkInterfaceState()
    data object Connected: NetworkInterfaceState()
    data object Disconnected: NetworkInterfaceState()
}
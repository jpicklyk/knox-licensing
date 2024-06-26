package net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model

sealed class NetworkInterfaceState {
    data object Connected : NetworkInterfaceState()
    data object Disconnected : NetworkInterfaceState()
    data object Unknown: NetworkInterfaceState()
}
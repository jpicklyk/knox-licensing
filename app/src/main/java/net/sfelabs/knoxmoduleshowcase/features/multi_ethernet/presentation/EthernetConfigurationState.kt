package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.presentation

import net.sfelabs.knox_tactical.domain.model.AutoConnectionState
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.EthernetInterface

data class EthernetConfigurationState (
    val isLoading: Boolean = false,
    val autoConnectionState: AutoConnectionState? = null,
    val ethInterface: EthernetInterface
    ) {
    fun isAutoConnectionEnabled():Boolean {
        return autoConnectionState == AutoConnectionState.ON
    }
}
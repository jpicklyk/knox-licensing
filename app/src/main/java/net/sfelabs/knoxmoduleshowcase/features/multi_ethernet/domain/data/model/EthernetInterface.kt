package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model

import net.sfelabs.common.core.model.NetworkStatus
import net.sfelabs.knox_tactical.domain.model.EthernetInterfaceType

data class EthernetInterface(
        val name: String,
        val type: EthernetInterfaceType,
        val netmask: String? = null,
        val ipAddress: String? = null,
        val gateway: String? = null,
        val mac: String? = null,
        val dnsList: String? = null,
        val connectivity: NetworkStatus = NetworkStatus.Unknown
)


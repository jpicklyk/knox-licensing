package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.services

import kotlinx.coroutines.flow.Flow
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.NetworkUpdate

interface NetworkConnectivityService {
    val networkUpdate: Flow<NetworkUpdate>
    fun registerInterface(interfaceName: String)
}
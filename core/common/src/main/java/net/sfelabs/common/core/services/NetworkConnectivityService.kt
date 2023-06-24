package net.sfelabs.common.core.services

import kotlinx.coroutines.flow.Flow
import net.sfelabs.common.core.model.NetworkUpdate

interface NetworkConnectivityService {
    val networkUpdate: Flow<NetworkUpdate>
}
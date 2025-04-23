package net.sfelabs.knox_enterprise.license.domain.repository

import kotlinx.coroutines.flow.StateFlow
import net.sfelabs.knox_enterprise.license.presentation.LicenseState

interface LicenseRepository {
    val licenseState: StateFlow<LicenseState>
    suspend fun refreshLicenseState()
    suspend fun activateLicense()
    suspend fun deactivateLicense()
}
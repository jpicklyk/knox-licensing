package com.github.jpicklyk.knox.licensing.data

import com.github.jpicklyk.knox.licensing.domain.KnoxLicenseHandler
import com.github.jpicklyk.knox.licensing.domain.LicenseConfiguration
import com.github.jpicklyk.knox.licensing.domain.LicenseInfo
import com.github.jpicklyk.knox.licensing.domain.LicenseResult
import com.github.jpicklyk.knox.licensing.domain.LicenseState
import kotlinx.coroutines.flow.Flow

internal class KnoxLicenseHandlerImpl(
    private val licenseConfiguration: LicenseConfiguration,
    private val knoxLicenseRepository: KnoxLicenseRepository
) : KnoxLicenseHandler {

    override suspend fun activate(licenseName: String): LicenseResult {
        return knoxLicenseRepository.activateLicense(licenseName)
    }

    override suspend fun deactivate(licenseName: String): LicenseResult {
        return knoxLicenseRepository.deactivateLicense(licenseName)
    }

    override suspend fun getLicenseInfo(): LicenseInfo {
        return knoxLicenseRepository.getLicenseInfo()
    }

    override fun observeLicenseState(): Flow<LicenseState> {
        return knoxLicenseRepository.licenseState
    }

    override fun getAvailableLicenses(): Map<String, String> {
        return licenseConfiguration.getAllKeys()
    }

    override fun hasLicense(licenseName: String): Boolean {
        return licenseConfiguration.hasKey(licenseName)
    }
}
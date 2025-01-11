package net.sfelabs.knox_enterprise.license.domain.usecase

import net.sfelabs.knox_enterprise.BuildConfig
import net.sfelabs.knox_enterprise.license.presentation.LicenseState

interface KnoxLicenseUseCase {
    suspend operator fun invoke(activate: Boolean = true, licenseKey: String = BuildConfig.KNOX_LICENSE_KEY): LicenseState
}
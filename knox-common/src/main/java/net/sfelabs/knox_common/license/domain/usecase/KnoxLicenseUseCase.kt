package net.sfelabs.knox_common.license.domain.usecase

import net.sfelabs.knox_common.BuildConfig
import net.sfelabs.knox_common.license.presentation.LicenseState

interface KnoxLicenseUseCase {
    suspend operator fun invoke(activate: Boolean = true, licenseKey: String = BuildConfig.KNOX_LICENSE_KEY): LicenseState
}
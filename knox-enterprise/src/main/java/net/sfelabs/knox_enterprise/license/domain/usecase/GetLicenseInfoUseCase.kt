package net.sfelabs.knox_enterprise.license.domain.usecase

import net.sfelabs.knox_enterprise.license.presentation.LicenseState

interface GetLicenseInfoUseCase {
    suspend operator fun invoke(): LicenseState
}
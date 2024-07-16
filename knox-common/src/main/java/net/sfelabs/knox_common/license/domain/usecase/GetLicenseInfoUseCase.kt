package net.sfelabs.knox_common.license.domain.usecase

import net.sfelabs.knox_common.license.presentation.LicenseState

interface GetLicenseInfoUseCase {
    suspend operator fun invoke(): LicenseState
}
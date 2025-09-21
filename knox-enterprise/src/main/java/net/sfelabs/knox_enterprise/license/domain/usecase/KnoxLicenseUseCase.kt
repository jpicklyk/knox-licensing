package net.sfelabs.knox_enterprise.license.domain.usecase

import net.sfelabs.knox_enterprise.license.presentation.LicenseState

/**
 * @deprecated Use the knox-licensing module instead for license management.
 * This interface is kept for backward compatibility but should not be used in new code.
 */
@Deprecated("Use knox-licensing module instead", ReplaceWith("KnoxLicenseHandler", "com.github.jpicklyk.knox.licensing.domain.KnoxLicenseHandler"))
interface KnoxLicenseUseCase {
    suspend operator fun invoke(activate: Boolean = true, licenseKey: String): LicenseState
}
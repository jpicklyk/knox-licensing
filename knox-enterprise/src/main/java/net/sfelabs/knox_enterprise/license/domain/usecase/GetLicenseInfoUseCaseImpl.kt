package net.sfelabs.knox_enterprise.license.domain.usecase

import android.content.Context
import com.samsung.android.knox.license.ActivationInfo
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager
import net.sfelabs.knox_enterprise.license.presentation.LicenseState
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GetLicenseInfoUseCaseImpl @Inject constructor(
    private val context: Context
) : GetLicenseInfoUseCase {
    override suspend operator fun invoke(): LicenseState {
        return try {
            val activationInfo = getKnoxManager().licenseActivationInfo

            when (activationInfo?.state) {
                null -> LicenseState.NotActivated
                ActivationInfo.State.ACTIVE -> LicenseState.Activated(
                    message = "Activation Date: ${activationInfo.activationDate}"
                )
                ActivationInfo.State.EXPIRED -> LicenseState.Expired
                ActivationInfo.State.TERMINATED -> LicenseState.Terminated
            }
        } catch (e: Exception) {
            LicenseState.Error("Failed to get license info: ${e.message}")
        }
    }

    private fun getKnoxManager(): KnoxEnterpriseLicenseManager {
        return KnoxEnterpriseLicenseManager.getInstance(context)
    }
}
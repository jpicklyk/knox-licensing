package net.sfelabs.knox_common.license.domain.usecase

import android.content.Context
import android.util.Log
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager
import com.samsung.android.knox.license.LicenseResultCallback
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import net.sfelabs.knox_common.license.data.KnoxErrorMapper
import net.sfelabs.knox_common.license.presentation.LicenseState
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
internal class KnoxLicenseUseCaseImpl @Inject constructor(
    private val context: Context,
    private val knoxErrorMapper: KnoxErrorMapper
) : KnoxLicenseUseCase {
    private val tag = "KnoxLicenseUseCaseImpl"
    override suspend operator fun invoke(activate: Boolean, licenseKey: String): LicenseState = suspendCancellableCoroutine { continuation ->
        val callback = LicenseResultCallback { licenseResult ->
            val state = when {
                licenseResult == null -> LicenseState.Error("License result is null")
                licenseResult.isSuccess ->
                    if (activate) LicenseState.Activated("License activated successfully")
                    else LicenseState.NotActivated
                else -> {
                    val errorMessage = knoxErrorMapper.getKpeErrorMessage(licenseResult.errorCode)
                    LicenseState.Error("$errorMessage. Details: ${licenseResult.errorCode}")
                }
            }
            continuation.resume(state)
        }

        try {
            val knoxManager = getKnoxManager()
            if (activate) {
                Log.d(tag, "Activating License: $licenseKey")
                knoxManager.activateLicense(licenseKey, callback)
            } else {
                Log.d(tag, "Deactivating License: $licenseKey")
                knoxManager.deActivateLicense(licenseKey, callback)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            val errorCode = when (e) {
                is SecurityException -> KnoxEnterpriseLicenseManager.ERROR_INTERNAL
                is IllegalArgumentException -> KnoxEnterpriseLicenseManager.ERROR_INVALID_LICENSE
                else -> KnoxEnterpriseLicenseManager.ERROR_UNKNOWN
            }
            continuation.resume(LicenseState.Error(knoxErrorMapper.getKpeErrorMessage(errorCode)))
        }
    }

    private fun getKnoxManager(): KnoxEnterpriseLicenseManager {
        return KnoxEnterpriseLicenseManager.getInstance(context)
    }
}
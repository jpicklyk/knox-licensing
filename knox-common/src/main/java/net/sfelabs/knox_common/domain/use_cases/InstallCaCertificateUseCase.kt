package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_common.domain.model.CertificateType
import net.sfelabs.knox_common.domain.model.TargetKeystore
import javax.inject.Inject

class InstallCaCertificateUseCase @Inject constructor(
    private val enterpriseDeviceManager: EnterpriseDeviceManager
) {

    operator fun invoke(
        keystore: TargetKeystore,
        certificateType: CertificateType,
        data: ByteArray,
        alias: String,
        password: String
    ): UnitApiCall {
        return try {
            val certificateProvisioning = enterpriseDeviceManager.certificateProvisioning
            val result = certificateProvisioning.installCertificateToKeystore(
                certificateType.type,
                data,
                alias,
                password,
                keystore.value
            )
            if (result) ApiResult.Success(Unit)
            else ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "Certificate failed to install to keystore"
                )
            )
        } catch (e: Exception) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "Knox API installCertificateToKeystore failed: ${e.message}"
                )
            )
        }
    }
}

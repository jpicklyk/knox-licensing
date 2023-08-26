package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.knox_common.domain.model.CertificateType
import net.sfelabs.knox_common.domain.model.TargetKeystore
import javax.inject.Inject

class InstallCaCertificateUseCase @Inject constructor(
    private val enterpriseDeviceManager: EnterpriseDeviceManager
) {

    suspend operator fun invoke(
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
            if (result) ApiCall.Success(Unit)
            else ApiCall.Error(UiText.DynamicString("Certificate failed to install to keystore"))
        } catch (e: Exception) {
            ApiCall.Error(
                UiText.DynamicString(
                    "Knox API installCertificateToKeystore failed: ${e.message}"
                )
            )
        }
    }
}

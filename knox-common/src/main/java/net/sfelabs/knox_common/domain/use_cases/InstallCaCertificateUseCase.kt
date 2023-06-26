package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.core.ui.UiText
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
    ): net.sfelabs.core.ui.UnitApiCall {
        return try {
            val certificateProvisioning = enterpriseDeviceManager.certificateProvisioning
            val result = certificateProvisioning.installCertificateToKeystore(
                certificateType.type,
                data,
                alias,
                password,
                keystore.value
            )
            if (result) net.sfelabs.core.ui.ApiCall.Success(Unit)
            else net.sfelabs.core.ui.ApiCall.Error(net.sfelabs.core.ui.UiText.DynamicString("Certificate failed to install to keystore"))
        } catch (e: Exception) {
            net.sfelabs.core.ui.ApiCall.Error(
                net.sfelabs.core.ui.UiText.DynamicString(
                    "Knox API installCertificateToKeystore failed: ${e.message}"
                ))
        }
    }
}

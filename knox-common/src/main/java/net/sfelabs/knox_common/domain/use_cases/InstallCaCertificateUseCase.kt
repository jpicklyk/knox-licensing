package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_common.domain.model.CertificateType
import net.sfelabs.knox_common.domain.model.TargetKeystore

class InstallCaCertificateUseCase: WithAndroidApplicationContext, CoroutineApiUseCase<InstallCaCertificateUseCase.Params, Unit>() {
    class Params(
        val keystore: TargetKeystore,
        val certificateType: CertificateType,
        val data: ByteArray,
        val alias: String,
        val password: String
    )

    private val enterpriseDeviceManager = EnterpriseDeviceManager.getInstance(applicationContext)

    suspend operator fun invoke(
        keystore: TargetKeystore,
        certificateType: CertificateType,
        data: ByteArray,
        alias: String,
        password: String
    ): UnitApiCall {
        return invoke(Params(keystore, certificateType, data, alias, password))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        val certificateProvisioning = enterpriseDeviceManager.certificateProvisioning
        val result = certificateProvisioning.installCertificateToKeystore(
            params.certificateType.type,
            params.data,
            params.alias,
            params.password,
            params.keystore.value
        )
        return if (result) ApiResult.Success(Unit)
        else ApiResult.Error(
            DefaultApiError.UnexpectedError(
                "Certificate failed to install to keystore"
            )
        )
    }
}

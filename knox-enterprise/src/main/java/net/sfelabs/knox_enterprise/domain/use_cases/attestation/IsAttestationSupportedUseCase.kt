package net.sfelabs.knox_enterprise.domain.use_cases.attestation

import com.samsung.android.knox.EnterpriseKnoxManager
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.api.domain.usecase.base.SuspendingUseCase

class IsAttestationSupportedUseCase: WithAndroidApplicationContext, SuspendingUseCase<Unit, Boolean>() {
    private val attestationPolicy =
        EnterpriseKnoxManager.getInstance(applicationContext).enhancedAttestationPolicy

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return ApiResult.Success(attestationPolicy.isSupported)
    }
}
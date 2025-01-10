package net.sfelabs.knox_common.domain.use_cases.attestation

import com.samsung.android.knox.EnterpriseKnoxManager
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase

class IsAttestationSupportedUseCase: WithAndroidApplicationContext, CoroutineApiUseCase<Unit, Boolean>() {
    private val attestationPolicy =
        EnterpriseKnoxManager.getInstance(applicationContext).enhancedAttestationPolicy

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return ApiResult.Success(attestationPolicy.isSupported)
    }
}
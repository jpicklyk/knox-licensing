package net.sfelabs.knox_common.domain.use_cases.attestation

import com.samsung.android.knox.EnterpriseKnoxManager
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult

class IsAttestationSupportedUseCase: KnoxContextAwareUseCase<Unit, Boolean>() {
    private val attestationPolicy =
        EnterpriseKnoxManager.getInstance(knoxContext).enhancedAttestationPolicy

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return ApiResult.Success(attestationPolicy.isSupported)
    }
}
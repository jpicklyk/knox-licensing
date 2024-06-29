package net.sfelabs.knox_common.domain.use_cases.attestation

import com.samsung.android.knox.integrity.EnhancedAttestationPolicy
import net.sfelabs.core.domain.api.ApiResult
import javax.inject.Inject

class IsAttestationSupportedUseCase @Inject constructor(
    private val attestationPolicy: EnhancedAttestationPolicy
) {
    operator fun invoke(): ApiResult<Boolean> {
        return ApiResult.Success(attestationPolicy.isSupported)
    }
}
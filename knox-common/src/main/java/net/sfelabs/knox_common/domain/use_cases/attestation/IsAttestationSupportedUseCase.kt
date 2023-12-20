package net.sfelabs.knox_common.domain.use_cases.attestation

import com.samsung.android.knox.integrity.EnhancedAttestationPolicy
import net.sfelabs.core.domain.ApiCall
import javax.inject.Inject

class IsAttestationSupportedUseCase @Inject constructor(
    private val attestationPolicy: EnhancedAttestationPolicy
) {
    operator fun invoke(): ApiCall<Boolean> {
        return ApiCall.Success(attestationPolicy.isSupported)
    }
}
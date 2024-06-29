package net.sfelabs.knox_common.domain.use_cases.attestation

import com.samsung.android.knox.integrity.EnhancedAttestationPolicy
import com.samsung.android.knox.integrity.EnhancedAttestationPolicyCallback
import com.samsung.android.knox.integrity.EnhancedAttestationResult
import com.samsung.android.knox.integrity.EnhancedAttestationResult.ERROR_NONE
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.suspendCoroutine

/**
 * Use case to wrap the asynchronous callback into sequential code since we are not attempting to
 * make an external call to a server.
 */
class GetAttestationBlobUseCase @Inject constructor(
    private val attestationPolicy: EnhancedAttestationPolicy
) {
    suspend operator fun invoke(nonce: String = UUID.randomUUID().toString()): ApiResult<ByteArray> {
        return suspendCoroutine {
            attestationPolicy.startAttestation(nonce, object: EnhancedAttestationPolicyCallback() {
                override fun onAttestationFinished(result: EnhancedAttestationResult) {
                    if(result.error == ERROR_NONE) {
                        it.resumeWith(Result.success(ApiResult.Success(result.blob)))
                    } else {
                        it.resumeWith(Result.success(
                            ApiResult.Error(UiText.DynamicString(
                            "Attestation error (${result.error}) was encountered with reason: ${result.reason}")
                        )))
                    }
                }

            })
        }
    }
}

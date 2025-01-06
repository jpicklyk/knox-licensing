package net.sfelabs.knox_common.domain.use_cases.attestation

import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.EnterpriseKnoxManager
import com.samsung.android.knox.integrity.EnhancedAttestationPolicyCallback
import com.samsung.android.knox.integrity.EnhancedAttestationResult
import com.samsung.android.knox.integrity.EnhancedAttestationResult.ERROR_NONE
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import java.util.UUID
import kotlin.coroutines.suspendCoroutine

/**
 * Use case to wrap the asynchronous callback into sequential code since we are not attempting to
 * make an external call to a server.
 */
class GetAttestationBlobUseCase: KnoxContextAwareUseCase<GetAttestationBlobUseCase.Params, ByteArray>() {
    data class Params(val nonce: String = UUID.randomUUID().toString())

    private val attestationPolicy =
        EnterpriseKnoxManager.getInstance(knoxContext).enhancedAttestationPolicy

    suspend operator fun invoke(nonce: String = UUID.randomUUID().toString()): ApiResult<ByteArray> {
        return invoke(Params(nonce))
    }

    override suspend fun execute(params: Params): ApiResult<ByteArray> {
        return suspendCoroutine {
            attestationPolicy.startAttestation(params.nonce, object: EnhancedAttestationPolicyCallback() {
                override fun onAttestationFinished(result: EnhancedAttestationResult) {
                    if(result.error == ERROR_NONE) {
                        it.resumeWith(Result.success(ApiResult.Success(result.blob)))
                    } else {
                        it.resumeWith(Result.success(
                            ApiResult.Error(
                                DefaultApiError.UnexpectedError(
                                    "Attestation error (${result.error}) was encountered with reason: ${result.reason}"
                                )
                            )))
                    }
                }

            })
        }
    }
}

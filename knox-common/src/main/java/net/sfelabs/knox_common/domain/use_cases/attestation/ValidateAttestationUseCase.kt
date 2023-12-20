package net.sfelabs.knox_common.domain.use_cases.attestation

import com.samsung.knox.attesation.blobvalidator.library.AttestationResult
import com.samsung.knox.attesation.blobvalidator.library.EABlobVerifier
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText

/**
 * Attestation verifier from HQ integrated into a simple API use case call.
 *
 * Currently the attestation result verdict will report No when HDM CP is disabled.
 *
 * NOTE: THIS IS NOT A SECURE FORM OF VALIDATING THE ATTESTATION BLOB.  AN EXTERNAL SERVER MUST BE
 * USED FOR ANY FORM OF TRUST!
 */
class ValidateAttestationUseCase {
    operator fun invoke(nonce: String, blob: ByteArray): ApiCall<AttestationResult> {
        return try {
            ApiCall.Success(EABlobVerifier.verify(nonce, blob))
        } catch (e: Exception) {
            ApiCall.Error(UiText.DynamicString(e.message!!))
        }
    }
}
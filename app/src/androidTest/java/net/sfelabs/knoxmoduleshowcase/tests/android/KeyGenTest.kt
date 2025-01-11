package net.sfelabs.knoxmoduleshowcase.tests.android

import androidx.test.filters.FlakyTest
import com.samsung.knox.attesation.blobvalidator.library.Verdict
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.knox_common.domain.use_cases.attestation.GetAttestationBlobUseCase
import net.sfelabs.knox_common.domain.use_cases.attestation.KeyGeneratorUseCase
import net.sfelabs.knox_common.domain.use_cases.attestation.ValidateAttestationUseCase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.UUID


class KeyGenTest {

    @Test
    @FlakyTest
    fun createCertificateTest() = runTest {
        val useCase = KeyGeneratorUseCase()
        println("Starting keygen stress test")
        for(i in 10 downTo 0 step 1) {
            val result = useCase.invoke()
            Assertions.assertTrue(
                result is ApiResult.Success,
                "Certificate generation failed: $result"
            )
            val nonce = UUID.randomUUID().toString()
            val blobResult = GetAttestationBlobUseCase().invoke(nonce)
            Assertions.assertTrue(blobResult is ApiResult.Success, "Unable to get attestation blob")
            if(blobResult is ApiResult.Success) {
                val attestationResult = ValidateAttestationUseCase().invoke(nonce, blobResult.data)
                assert(attestationResult is ApiResult.Success
                        && attestationResult.data.verdict == Verdict.Yes)
            }
        }
        println("Completed keygen stress test")
    }
}
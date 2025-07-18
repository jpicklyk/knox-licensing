package net.sfelabs.knoxmoduleshowcase.tests.android

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.FlakyTest
//import com.samsung.knox.attesation.blobvalidator.library.Verdict
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_enterprise.domain.use_cases.attestation.GetAttestationBlobUseCase
import net.sfelabs.knox_enterprise.domain.use_cases.attestation.KeyGeneratorUseCase
import net.sfelabs.knox_enterprise.domain.use_cases.attestation.ValidateAttestationUseCase
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID


@RunWith(AndroidJUnit4::class)
class KeyGenTest {

//    @Test
//    @FlakyTest
//    fun createCertificateTest() = runTest {
//        val useCase = KeyGeneratorUseCase()
//        println("Starting keygen stress test")
//        for(i in 10 downTo 0 step 1) {
//            val result = useCase.invoke()
//            Assert.assertTrue(
//                "Certificate generation failed: $result",
//                result is ApiResult.Success
//            )
//            val nonce = UUID.randomUUID().toString()
//            val blobResult = GetAttestationBlobUseCase().invoke(nonce)
//            Assert.assertTrue("Unable to get attestation blob", blobResult is ApiResult.Success)
//            if(blobResult is ApiResult.Success) {
//                val attestationResult = ValidateAttestationUseCase().invoke(nonce,
//                    blobResult.data
//                )
//                assert(attestationResult is ApiResult.Success
//                        && attestationResult.data.verdict == Verdict.Yes)
//            }
//        }
//        println("Completed keygen stress test")
//    }
}
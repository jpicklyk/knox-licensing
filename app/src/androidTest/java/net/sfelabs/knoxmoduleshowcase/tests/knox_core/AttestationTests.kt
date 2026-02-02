package net.sfelabs.knoxmoduleshowcase.tests.knox_core

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
//import com.samsung.knox.attesation.blobvalidator.library.Verdict
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_enterprise.domain.use_cases.attestation.GetAttestationBlobUseCase
import net.sfelabs.knox_enterprise.domain.use_cases.attestation.IsAttestationSupportedUseCase
import net.sfelabs.knox_tactical.domain.use_cases.attestation.ValidateAttestationUseCase
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
@SmallTest
class AttestationTests {

    @Test
    fun isAttestationSupported() = runTest {
        val useCase = IsAttestationSupportedUseCase()
        val result = useCase.invoke()
        assert(result is ApiResult.Success && result.data)
    }

//    @Test
//    fun validateAttestationBlob() = runTest {
//        val nonce = UUID.randomUUID().toString()
//        val useCaseResult = GetAttestationBlobUseCase().invoke(nonce)
//        assert(useCaseResult is ApiResult.Success)
//        if(useCaseResult is ApiResult.Success) {
//            val result = ValidateAttestationUseCase().invoke(nonce, useCaseResult.data)
//            assert(result is ApiResult.Success)
//            if(result is ApiResult.Success) {
//                val blob = result.data
//                val text = StringBuffer("Blob Validation: \n")
//                    .append("version: ${blob.version} \n")
//                    .append("nonce: ${blob.nonce} \n")
//                    .append("verdict: ${blob.verdict} \n")
//                    .append("warranty fuse blown?: ${blob.warrantyFuseState} \n")
//                    .append("trust boot state: ${blob.trustBootState} \n")
//                    .append("device id state: ${blob.deviceIdState} \n")
//                println(text)
//                assertTrue("Attestation verdict is no!",blob.verdict == Verdict.Yes)
//            }
//
//        }
//
//    }
}
package net.sfelabs.knoxmoduleshowcase.tests.android

import android.app.admin.DevicePolicyManager
import android.content.Context
import androidx.test.filters.FlakyTest
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.integrity.EnhancedAttestationPolicy
import com.samsung.knox.attesation.blobvalidator.library.Verdict
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.knox_common.di.KnoxModule
import net.sfelabs.knox_common.domain.use_cases.attestation.GetAttestationBlobUseCase
import net.sfelabs.knox_common.domain.use_cases.attestation.KeyGeneratorUseCase
import net.sfelabs.knox_common.domain.use_cases.attestation.ValidateAttestationUseCase
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID


class KeyGenTest {
    private lateinit var context: Context
    private lateinit var attestationPolicy: EnhancedAttestationPolicy

    @BeforeEach
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        attestationPolicy = KnoxModule.provideAttestationPolicy(context)
    }


    @Test
    @FlakyTest
    fun createCertificateTest() = runTest {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val useCase = KeyGeneratorUseCase(dpm)
        println("Starting keygen stress test")
        for(i in 10 downTo 0 step 1) {
            val result = useCase.invoke()
            Assertions.assertTrue(
                result is ApiResult.Success,
                "Certificate generation failed: $result"
            )
            val nonce = UUID.randomUUID().toString()
            val blobResult = GetAttestationBlobUseCase(attestationPolicy).invoke(nonce)
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
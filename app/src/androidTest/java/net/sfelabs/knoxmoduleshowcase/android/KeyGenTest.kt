package net.sfelabs.knoxmoduleshowcase.android

import android.app.admin.DevicePolicyManager
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.integrity.EnhancedAttestationPolicy
import com.samsung.knox.attesation.blobvalidator.library.Verdict
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.use_cases.keystore.KeyGeneratorUseCase
import net.sfelabs.knox_common.di.KnoxModule
import net.sfelabs.knox_common.domain.use_cases.attestation.GetAttestationBlobUseCase
import net.sfelabs.knox_common.domain.use_cases.attestation.ValidateAttestationUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.UUID

@RunWith(AndroidJUnit4::class)
@SmallTest
class KeyGenTest {
    private lateinit var context: Context
    private lateinit var attestationPolicy: EnhancedAttestationPolicy

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        attestationPolicy = KnoxModule.provideAttestationPolicy(context)
    }


    @Test
    fun createCertificateTest() = runTest {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val useCase = KeyGeneratorUseCase(dpm)
        println("Starting keygen stress test")
        for(i in 13 downTo 0 step 1) {
            val result = useCase.invoke()
            assertTrue("Certificate generation failed: $result", result is ApiCall.Success)
            val nonce = UUID.randomUUID().toString()
            val blobResult = GetAttestationBlobUseCase(attestationPolicy).invoke(nonce)
            assertTrue("Unable to get attestation blob", blobResult is ApiCall.Success)
            if(blobResult is ApiCall.Success) {
                val attestationResult = ValidateAttestationUseCase().invoke(nonce, blobResult.data)
                assert(attestationResult is ApiCall.Success
                        && attestationResult.data.verdict == Verdict.Yes)
            }
        }
        println("Completed keygen stress test")
    }
}
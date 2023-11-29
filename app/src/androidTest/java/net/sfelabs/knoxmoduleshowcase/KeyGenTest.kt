package net.sfelabs.knoxmoduleshowcase

import android.app.admin.DevicePolicyManager
import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.use_cases.keystore.KeyGeneratorUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class KeyGenTest {
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }


    @Test
    fun createCertificateTest() = runTest {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val useCase = KeyGeneratorUseCase(dpm)
        println("Starting keygen stress test")
        for(i in 10 downTo 0 step 1) {
            val result = useCase.invoke()
            assertTrue("Certificate generation failed: ${result.toString()}", result is ApiCall.Success)
        }
        println("Completed keygen stress test")
    }
}
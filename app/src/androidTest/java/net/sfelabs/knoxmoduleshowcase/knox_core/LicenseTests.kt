package net.sfelabs.knoxmoduleshowcase.knox_core

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.license.ActivationInfo
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_common.di.KnoxModule
import net.sfelabs.knox_common.domain.use_cases.license.GetLicenseActivationInfoUseCase
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LicenseTests {
    private lateinit var context: Context
    private lateinit var licenseManager: KnoxEnterpriseLicenseManager
    private val license = "KLM05-6J7VR-PN3X1-W76OG-GOHYF-9MVLZ-000X-D6090-A4GSS-3SUAP#knox.sdsasolutions.io"

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        licenseManager = KnoxModule.provideKnoxEnterpriseLicenseManager(context)
    }

    @Test
    fun activateLicense() {
        licenseManager.activateLicense(license)
    }

    @Test
    fun checkActivationInfo() = runTest{
        val result = GetLicenseActivationInfoUseCase(licenseManager).invoke()
        if(result is ApiCall.Success) {
            val info = result.data
            println(info)
            assert(ActivationInfo.State.ACTIVE == info.state)
        } else {
            assertTrue("No license info is available", false)
        }
    }

    @Test
    fun deactivateLicense() {
        licenseManager.deActivateLicense(license)
    }
}
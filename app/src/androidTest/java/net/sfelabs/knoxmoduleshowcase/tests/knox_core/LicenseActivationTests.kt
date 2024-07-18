package net.sfelabs.knoxmoduleshowcase.tests.knox_core

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox_common.license.domain.usecase.GetLicenseInfoUseCase
import net.sfelabs.knox_common.license.domain.usecase.KnoxLicenseUseCase
import net.sfelabs.knox_common.license.presentation.LicenseState
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 130)
@RunWith(AndroidJUnit4::class)
class LicenseActivationTests {
    private val te3Key1 = "KLM05-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX#offline.com"
    private val te3Key2 = "KLM09-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX"
    private val expiredKey = "KLM06-0S63S-5B040-PSSXS-2JHYD-21IRP"
    private val productionCloudKey = "KLM06-DZMJX-2ZOPV-NINE6-PUBIH-TWVP9"

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var knoxLicenseUseCase: KnoxLicenseUseCase
    @Inject
    lateinit var licenseInfoUseCase: GetLicenseInfoUseCase

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun activateOfflineTe3Key_returnSuccess() = runTest {
        val activateResult = activateLicense(licenseKey = te3Key1)
        assertTrue("License activation failed. ${activateResult.getErrorOrNull()}", activateResult.isActivated())

        val deactivateResult = deactivateLicense(licenseKey = te3Key1)
        assertTrue("License not deactivated. ${deactivateResult.getErrorOrNull()}",deactivateResult.isNotActivated())
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun activateTe3Key_returnSuccess() = runTest {
        val activateResult = activateLicense(licenseKey = te3Key2)
        assertTrue("License activation failed. ${activateResult.getErrorOrNull()}", activateResult.isActivated())

        val deactivateResult = deactivateLicense(licenseKey = te3Key2)
        assertTrue("License not deactivated. ${deactivateResult.getErrorOrNull()}",deactivateResult.isNotActivated())
    }

//    @Test
//    fun activateExpiredKey_returnExpired() = runTest {
//        val activateResult = activateLicense(licenseKey = expiredKey)
//        assertTrue("License didn't return expired. Result: ${activateResult.getErrorOrNull()}", activateResult.isExpired())
//    }

    @Test
    fun activateCloudProductionKey_returnSuccess() = runTest {
        val activateResult = activateLicense(licenseKey = productionCloudKey)
        assertTrue("License activation failed. ${activateResult.getErrorOrNull()}", activateResult.isActivated())

        val deactivateResult = deactivateLicense(licenseKey = productionCloudKey)
        assertTrue("License not deactivated. ${deactivateResult.getErrorOrNull()}",deactivateResult.isNotActivated())
    }

    private suspend fun activateLicense(licenseKey: String) : LicenseState {
        return knoxLicenseUseCase(activate = true, licenseKey = licenseKey)
    }

    private suspend fun deactivateLicense(licenseKey: String) : LicenseState {
        return knoxLicenseUseCase(activate = false, licenseKey = licenseKey)
    }

    private suspend fun getLicenseInfo(licenseKey: String) {
        val activateInfo = licenseInfoUseCase()
        assert(activateInfo.isActivated())
    }

}
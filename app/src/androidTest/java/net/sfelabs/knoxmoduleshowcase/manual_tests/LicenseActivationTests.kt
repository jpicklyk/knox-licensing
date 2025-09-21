package net.sfelabs.knoxmoduleshowcase.manual_tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import com.github.jpicklyk.knox.licensing.domain.KnoxLicenseHandler
import com.github.jpicklyk.knox.licensing.domain.LicenseResult
import com.github.jpicklyk.knox.licensing.domain.LicenseInfo
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@TacticalSdkSuppress(minReleaseVersion = 130)
@RunWith(AndroidJUnit4::class)
class LicenseActivationTests {
    private val te3Key1 = "KLM05-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX#offline.com"
    private val te3Key2 = "KLM09-XXXXX-XXXXX-XXXXX-XXXXX-XXXXX"
    //private val expiredKey = "KLM06-0S63S-5B040-PSSXS-2JHYD-21IRP"
    private val productionCloudKey = "KLM06-DZMJX-2ZOPV-NINE6-PUBIH-TWVP9"

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var knoxLicenseHandler: KnoxLicenseHandler

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun activateOfflineTe3Key_returnSuccess() = runTest {
        val activateResult = activateLicense()
        Assert.assertTrue(
            "License activation failed. ${getErrorMessage(activateResult)}",
            isSuccess(activateResult)
        )

        val deactivateResult = deactivateLicense()
        Assert.assertTrue(
            "License not deactivated. ${getErrorMessage(deactivateResult)}",
            isSuccess(deactivateResult)
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun activateTe3Key_returnSuccess() = runTest {
        val activateResult = activateLicense()
        Assert.assertTrue(
            "License activation failed. ${getErrorMessage(activateResult)}",
            isSuccess(activateResult)
        )

        val deactivateResult = deactivateLicense()
        Assert.assertTrue(
            "License not deactivated. ${getErrorMessage(deactivateResult)}",
            isSuccess(deactivateResult)
        )
    }

//    @Test
//    fun activateExpiredKey_returnExpired() = runTest {
//        val activateResult = activateLicense(licenseKey = expiredKey)
//        assertTrue("License didn't return expired. Result: ${activateResult.getErrorOrNull()}", activateResult.isExpired())
//    }

    @Test
    fun activateCloudProductionKey_returnSuccess() = runTest {
        val activateResult = activateLicense()
        Assert.assertTrue(
            "License activation failed. ${getErrorMessage(activateResult)}",
            isSuccess(activateResult)
        )

        val deactivateResult = deactivateLicense()
        Assert.assertTrue(
            "License not deactivated. ${getErrorMessage(deactivateResult)}",
            isSuccess(deactivateResult)
        )
    }

    private suspend fun activateLicense(): LicenseResult {
        // New knox-licensing module automatically selects appropriate license based on device type
        return knoxLicenseHandler.activate()
    }

    private suspend fun deactivateLicense(): LicenseResult {
        // New knox-licensing module automatically selects appropriate license based on device type
        return knoxLicenseHandler.deactivate()
    }

    private fun isSuccess(result: LicenseResult): Boolean {
        return result is LicenseResult.Success
    }

    private fun getErrorMessage(result: LicenseResult): String {
        return when (result) {
            is LicenseResult.Success -> "Success: ${result.message}"
            is LicenseResult.Error -> "Error: ${result.message}"
        }
    }

}
package net.sfelabs.knoxmoduleshowcase.tests.charging

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.charging.EnableFastChargingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.charging.IsFastChargingEnabledUseCase
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 140)
@RunWith(AndroidJUnit4::class)
class ChargingTests {

    @Test
    fun disableFastCharging() = runTest {
        val getUseCase = IsFastChargingEnabledUseCase()
        val setUseCase = EnableFastChargingUseCase()

        val setResult = setUseCase(false)
        assertTrue(
            "API result was not successful, " +
                    "${setResult.getErrorOrNull()}", setResult is ApiResult.Success
        )
        val isEnabled = getUseCase()
        assertTrue(
            "Fast charging is enabled!",
            isEnabled is ApiResult.Success && !isEnabled.data
        )
    }

    @Test
    fun enableFastCharging() = runTest {
        val getUseCase = IsFastChargingEnabledUseCase()
        val setUseCase = EnableFastChargingUseCase()

        val setResult = setUseCase(true)
        assertTrue(
            "API result was not successful, " +
                    "${setResult.getErrorOrNull()}", setResult is ApiResult.Success
        )
        val isEnabled = getUseCase()
        assertTrue(
            "Fast charging is disabled!",
            isEnabled is ApiResult.Success && isEnabled.data
        )
    }
}
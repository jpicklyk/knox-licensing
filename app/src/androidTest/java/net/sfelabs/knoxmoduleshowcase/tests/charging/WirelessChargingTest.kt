package net.sfelabs.knoxmoduleshowcase.tests.charging

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import junit.framework.TestCase.fail
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.charging.DisableWirelessChargingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.charging.IsWirelessChargingDisabledUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 141, excludeModels = ["SM-X308U", "SM-G736U1"])
class WirelessChargingTest {

    @Test
    fun disableWirelessCharging() = runTest {
        val setUseCase = DisableWirelessChargingUseCase()
        val getUseCase = IsWirelessChargingDisabledUseCase()

        when (val setResult = setUseCase(true)) {
            is ApiResult.Success -> { /* continue */ }
            else -> fail("disableWirelessCharging(true) returned: $setResult")
        }

        when (val getResult = getUseCase()) {
            is ApiResult.Success -> assertTrue("Wireless charging should be disabled", getResult.data)
            else -> fail("isWirelessChargingDisabled returned: $getResult")
        }
    }

    @Test
    fun enableWirelessCharging() = runTest {
        val setUseCase = DisableWirelessChargingUseCase()
        val getUseCase = IsWirelessChargingDisabledUseCase()

        when (val setResult = setUseCase(false)) {
            is ApiResult.Success -> { /* continue */ }
            else -> fail("disableWirelessCharging(false) returned: $setResult")
        }

        when (val getResult = getUseCase()) {
            is ApiResult.Success -> assertFalse("Wireless charging should be enabled", getResult.data)
            else -> fail("isWirelessChargingDisabled returned: $getResult")
        }
    }
}

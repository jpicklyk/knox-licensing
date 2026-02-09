package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SettingsManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

@SmallTest
@ApiExists
class ChargingApiExistenceTests {
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 141, excludeModels = ["SM-X308U"])
    fun disableWirelessCharging_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "disableWirelessCharging")) { "Expected method 'disableWirelessCharging' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 141, excludeModels = ["SM-X308U"])
    fun isWirelessChargingDisabled_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "isWirelessChargingDisabled")) { "Expected method 'isWirelessChargingDisabled' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 140)
    fun enableFastCharging_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "enableFastCharging")) { "Expected method 'enableFastCharging' to exist on SettingsManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 140)
    fun isFastChargingEnabled_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "isFastChargingEnabled")) { "Expected method 'isFastChargingEnabled' to exist on SettingsManager" }
    }
}

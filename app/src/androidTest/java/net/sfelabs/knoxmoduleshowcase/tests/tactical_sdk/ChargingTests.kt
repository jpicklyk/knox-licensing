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
@TacticalSdkSuppress(minReleaseVersion = 100)
class ChargingTests {
    @Test
    fun disableWirelessCharging_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "disableWirelessCharging"))
    }

    @Test
    fun isWirelessChargingDisabled_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "isWirelessChargingDisabled"))
    }

    @Test
    fun enableFastCharging_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "enableFastCharging"))
    }

    @Test
    fun isFastChargingEnabled_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "isFastChargingEnabled"))
    }
}

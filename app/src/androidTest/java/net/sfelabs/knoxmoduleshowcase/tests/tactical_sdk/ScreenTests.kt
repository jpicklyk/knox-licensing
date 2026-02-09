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
class ScreenTests {
    @Test
    fun getLcdBacklightState_Exists() = runTest {
        val kClass = SystemManager::class
        assert(checkMethodExistence(kClass, "getLcdBacklightState"))
    }

    @Test
    fun setLcdBacklightState_Exists() = runTest {
        val kClass = SystemManager::class
        assert(checkMethodExistence(kClass, "setLcdBacklightState"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun setActivityTime_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setActivityTime"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun getActivityTime_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getActivityTime"))
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131, excludeModels = ["SM-X308U"])
    fun setExtraBrightness_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "setExtraBrightness"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131, excludeModels = ["SM-X308U"])
    fun getExtraBrightness_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "getExtraBrightness"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun getNightVisionModeState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getNightVisionModeState"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun setNightVisionModeState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setNightVisionModeState"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun getAutoAdjustTouchSensitivity_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "getAutoAdjustTouchSensitivity"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun setAutoAdjustTouchSensitivity_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "setAutoAdjustTouchSensitivity"))
    }
}

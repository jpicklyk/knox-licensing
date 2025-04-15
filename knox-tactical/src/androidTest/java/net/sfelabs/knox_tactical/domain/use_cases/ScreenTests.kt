package net.sfelabs.knox_tactical.domain.use_cases

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SettingsManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.annotations.ApiExists
import net.sfelabs.core.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
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
}
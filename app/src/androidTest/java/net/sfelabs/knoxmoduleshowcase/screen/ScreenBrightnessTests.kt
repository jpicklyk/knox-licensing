package net.sfelabs.knoxmoduleshowcase.screen

import android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
import android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_common.domain.use_cases.settings.GetBrightnessModeUseCase
import net.sfelabs.knox_common.domain.use_cases.settings.GetBrightnessValueUseCase
import net.sfelabs.knox_common.domain.use_cases.settings.SetBrightnessUseCase
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.screen.GetExtraBrightnessUseCase
import net.sfelabs.knox_tactical.domain.use_cases.screen.SetExtraBrightnessUseCase
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ScreenBrightnessTests {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val settingsManager = KnoxModule.provideKnoxSettingsManager()
    @Test
    fun setBrightnessTo50() = runTest {
        val result = SetBrightnessUseCase(settingsManager).invoke(true, 50)
        assert(result is ApiResult.Success)
        val result2 = GetBrightnessValueUseCase(context).invoke()
        assert(result2 is ApiResult.Success && result2.data == 50)
        val result3 = GetBrightnessModeUseCase(context).invoke()
        assert(result3 is ApiResult.Success && result3.data == SCREEN_BRIGHTNESS_MODE_MANUAL)
    }

    @Test
    fun setBrightnessToAdaptive() = runTest {
        val result = SetBrightnessUseCase(settingsManager).invoke(false)
        assert(result is ApiResult.Success)
        val result2 = GetBrightnessModeUseCase(context).invoke()
        assert(result2 is ApiResult.Success && result2.data == SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun setExtraBrightness_On() = runTest {
        setBrightnessTo50()
        val result = SetExtraBrightnessUseCase(settingsManager).invoke(true)
        assert(result is ApiResult.Success)

        val result2 = GetExtraBrightnessUseCase(settingsManager).invoke()
        assert(result2 is ApiResult.Success && result2.data.value)
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun setExtraBrightness_Off() = runTest {
        setBrightnessTo50()
        val result = SetExtraBrightnessUseCase(settingsManager).invoke(false)
        assert(result is ApiResult.Success)

        val result2 = GetExtraBrightnessUseCase(settingsManager).invoke()
        assert(result2 is ApiResult.Success && !result2.data.value)
    }

    @After
    fun cleanup() = runTest {
        SetBrightnessUseCase(settingsManager).invoke(false)
        SetExtraBrightnessUseCase(settingsManager).invoke(false)
    }
}
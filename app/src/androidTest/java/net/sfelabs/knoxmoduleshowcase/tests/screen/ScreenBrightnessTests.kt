package net.sfelabs.knoxmoduleshowcase.tests.screen

import android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC
import android.provider.Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.knox_common.domain.use_cases.settings.GetBrightnessModeUseCase
import net.sfelabs.knox_common.domain.use_cases.settings.GetBrightnessValueUseCase
import net.sfelabs.knox_common.domain.use_cases.settings.SetBrightnessUseCase
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.screen.GetExtraBrightnessUseCase
import net.sfelabs.knox_tactical.domain.use_cases.screen.SetExtraBrightnessUseCase
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class ScreenBrightnessTests {
    @Test
    fun setBrightnessTo50() = runTest {
        val result = SetBrightnessUseCase().invoke(true, 50)
        assert(result is ApiResult.Success)
        val result2 = GetBrightnessValueUseCase().invoke()
        assert(result2 is ApiResult.Success && result2.data == 50)
        val result3 = GetBrightnessModeUseCase().invoke()
        assert(result3 is ApiResult.Success && result3.data == SCREEN_BRIGHTNESS_MODE_MANUAL)
    }

    @Test
    fun setBrightnessToAdaptive() = runTest {
        val result = SetBrightnessUseCase().invoke(false)
        assert(result is ApiResult.Success)
        val result2 = GetBrightnessModeUseCase().invoke()
        assert(result2 is ApiResult.Success && result2.data == SCREEN_BRIGHTNESS_MODE_AUTOMATIC)
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun setExtraBrightness_On() = runTest {
        setBrightnessTo50()
        val result = SetExtraBrightnessUseCase().invoke(true)
        assert(result is ApiResult.Success)

        val result2 = GetExtraBrightnessUseCase().invoke()
        assert(result2 is ApiResult.Success && result2.data)
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun setExtraBrightness_Off() = runTest {
        setBrightnessTo50()
        val result = SetExtraBrightnessUseCase().invoke(false)
        assert(result is ApiResult.Success)

        val result2 = GetExtraBrightnessUseCase().invoke()
        assert(result2 is ApiResult.Success && !result2.data)
    }

    @After
    fun cleanup() = runTest {
        SetBrightnessUseCase().invoke(false)
        SetExtraBrightnessUseCase().invoke(false)
    }
}
package net.sfelabs.knoxmoduleshowcase.tests.screen

import android.provider.Settings
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.di.AndroidServiceModule
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_enterprise.domain.use_cases.settings.GetBrightnessValueUseCase
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.backlight.GetLcdBacklightEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.backlight.SetLcdBacklightEnabledUseCase
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 100)
class LcdBacklightStateTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val powerManager = AndroidServiceModule.providePowerManager(context = context)

    @Test
    fun enableLcdBacklight() = runTest {
        val result = SetLcdBacklightEnabledUseCase().invoke(true)
        assert(result is ApiResult.Success)

        val result2 = GetLcdBacklightEnabledUseCase().invoke()
        assert(result2 is ApiResult.Success && result2.data)
        val brightness = Settings.System.getInt(
            context.contentResolver, Settings.System.SCREEN_BRIGHTNESS
        )
        println("Screen brightness settings set to $brightness")
        val result3 = GetBrightnessValueUseCase().invoke()
        if(result3 is ApiResult.Success) {
            println("Screen brightness set to ${result3.data}")
            @Suppress("DEPRECATION")
            println("Is screen on? ${powerManager.isScreenOn}")
            println("Is interactive? ${powerManager.isInteractive}")
        }
    }

    @Test
    fun disableLcdBacklightTest() = runTest {

        val result = SetLcdBacklightEnabledUseCase().invoke(false)
        assert(result is ApiResult.Success)

        val result2 = GetLcdBacklightEnabledUseCase().invoke()
        assert(result2 is ApiResult.Success && !result2.data)

        val brightness = Settings.System.getInt(
            context.contentResolver, Settings.System.SCREEN_BRIGHTNESS
        )
        println("Screen brightness settings set to $brightness")
        @Suppress("DEPRECATION")
        println("Is screen on? ${powerManager.isScreenOn}")
        println("Is interactive? ${powerManager.isInteractive}")
        val result3 = GetBrightnessValueUseCase().invoke()
        if(result3 is ApiResult.Success) {
            println("Screen brightness set to ${result3.data}")
        }
    }

    @After
    fun resetScreenOn() = runTest {
        Thread.sleep(2000)
        enableLcdBacklight()
    }
}
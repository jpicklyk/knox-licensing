package net.sfelabs.knoxmoduleshowcase.screen

import android.provider.Settings
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.di.AndroidServiceModule
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_common.domain.use_cases.settings.GetBrightnessValueUseCase
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.backlight.GetBacklightStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.backlight.SetBacklightStateUseCase
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 100)
class LcdBacklightStateTest {
    private val sm = KnoxModule.provideKnoxSystemManager()
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val powerManager = AndroidServiceModule.providePowerManager(context = context)

    @Test
    fun enableLcdBacklight() = runTest {
        val result = SetBacklightStateUseCase(sm).invoke(true)
        assert(result is ApiCall.Success)

        val result2 = GetBacklightStateUseCase(sm).invoke()
        assert(result2 is ApiCall.Success && result2.data)
        val brightness = Settings.System.getInt(
            context.contentResolver, Settings.System.SCREEN_BRIGHTNESS
        )
        println("Screen brightness settings set to $brightness")
        val result3 = GetBrightnessValueUseCase(context).invoke()
        if(result3 is ApiCall.Success) {
            println("Screen brightness set to ${result3.data}")
            println("Is screen on? ${powerManager.isScreenOn}")
            println("Is interactive? ${powerManager.isInteractive}")
        }
    }

    @Test
    fun disableLcdBacklightTest() = runTest {

        val result = SetBacklightStateUseCase(sm).invoke(false)
        assert(result is ApiCall.Success)

        val result2 = GetBacklightStateUseCase(sm).invoke()
        assert(result2 is ApiCall.Success && !result2.data)

        val brightness = Settings.System.getInt(
            context.contentResolver, Settings.System.SCREEN_BRIGHTNESS
        )
        println("Screen brightness settings set to $brightness")
        println("Is screen on? ${powerManager.isScreenOn}")
        println("Is interactive? ${powerManager.isInteractive}")
        val result3 = GetBrightnessValueUseCase(context).invoke()
        if(result3 is ApiCall.Success) {
            println("Screen brightness set to ${result3.data}")
        }
    }

    @After
    fun resetScreenOn() = runTest {
        Thread.sleep(2000)
        enableLcdBacklight()
    }
}
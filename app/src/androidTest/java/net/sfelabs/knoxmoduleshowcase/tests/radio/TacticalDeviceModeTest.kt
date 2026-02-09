package net.sfelabs.knoxmoduleshowcase.tests.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.tdm.GetTacticalDeviceModeEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tdm.SetTacticalDeviceModeEnabledUseCase
import org.junit.AfterClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 100)
@SmallTest
class TacticalDeviceModeTest {

    @Test
    fun enableTacticalDeviceMode() = runTest {
        val setResult = SetTacticalDeviceModeEnabledUseCase().invoke(true)
        assert(setResult is ApiResult.Success) {
            "Failed to enable tactical device mode: $setResult"
        }

        val getResult = GetTacticalDeviceModeEnabledUseCase().invoke()
        assert(getResult is ApiResult.Success && getResult.data) {
            "Tactical device mode should be enabled after setting, got: $getResult"
        }
    }

    @Test
    fun disableTacticalDeviceMode() = runTest {
        val setResult = SetTacticalDeviceModeEnabledUseCase().invoke(false)
        assert(setResult is ApiResult.Success) {
            "Failed to disable tactical device mode: $setResult"
        }

        val getResult = GetTacticalDeviceModeEnabledUseCase().invoke()
        assert(getResult is ApiResult.Success && !getResult.data) {
            "Tactical device mode should be disabled after setting, got: $getResult"
        }
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun resetDeviceState() {
            runBlocking {
                //SetTacticalDeviceModeEnabledUseCase().invoke(false)
            }
            // Allow the system to finish internal state changes after disabling tactical device mode
            //Doesn't seem to work for some reason.
//            Thread.sleep(5_000)
//            val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
//            uiDevice.executeShellCommand("settings put global airplane_mode_on 0")
//            uiDevice.executeShellCommand("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false")
        }
    }
}

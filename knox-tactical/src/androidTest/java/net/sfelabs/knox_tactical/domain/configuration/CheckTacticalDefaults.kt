package net.sfelabs.knox_tactical.domain.configuration

import android.provider.Settings
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.ramplus.GetRamPlusDisabledStateUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CheckTacticalDefaults {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun ramPlusState_Disabled() = runTest {
        val getUseCase = GetRamPlusDisabledStateUseCase(systemManager)
        val result2 = getUseCase.invoke()
        assert(result2 is ApiResult.Success && !result2.data.enabled)
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun airplaneMode_Enabled() = runTest {
        assert(Settings.Global.getInt(context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0) != 0)
    }
}
package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import android.provider.Settings
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.ramplus.GetRamPlusDisabledStateUseCase
import org.junit.Test
import kotlin.test.assertTrue

@SmallTest
class CheckTacticalDefaults {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130, excludeModels = ["SM-X308U"])
    fun ramPlusState_Disabled() = runTest {
        val getUseCase = GetRamPlusDisabledStateUseCase()
        val result = getUseCase.invoke()
        assertTrue(
            "Expected RAM Plus to be disabled, but got: $result"
        ) { result is ApiResult.Success && result.data }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun airplaneMode_Enabled() = runTest {
        assert(Settings.Global.getInt(context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON, 0) != 0) { "Expected airplane mode to be enabled by default" }
    }
}

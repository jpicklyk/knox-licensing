package net.sfelabs.knoxmoduleshowcase.tests.screen

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.auto_touch.GetAutoTouchSensitivityUseCase
import net.sfelabs.knox_tactical.domain.use_cases.auto_touch.SetAutoTouchSensitivityUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 110)
class AutoTouchSensitivityTest {
    private val sm = KnoxModule.provideKnoxSettingsManager()

    @Test
    fun enableAutoTouchSensitivity() = runTest {
        val setUseCase = SetAutoTouchSensitivityUseCase(sm)
        val getUseCase = GetAutoTouchSensitivityUseCase(sm)
        assert(setUseCase(true) is ApiResult.Success)
        val result = getUseCase.invoke()
        assert(result is ApiResult.Success)
        when(result) {
            is ApiResult.Success -> {
                assert(result.data.enabled)
            }
            else -> assert(false)
        }
    }

    @Test
    fun disableAutoTouchSensitivity() = runTest {
        val setUseCase = SetAutoTouchSensitivityUseCase(sm)
        val getUseCase = GetAutoTouchSensitivityUseCase(sm)
        assert(setUseCase(false) is ApiResult.Success)
        val result = getUseCase.invoke()
        assert(result is ApiResult.Success)
        when(result) {
            is ApiResult.Success -> {
                assert(!result.data.enabled)
            }
            else -> assert(false)
        }
    }
}
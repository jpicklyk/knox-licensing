package net.sfelabs.knoxmoduleshowcase

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.auto_touch.GetAutoTouchSensitivityUseCase
import net.sfelabs.knox_tactical.domain.use_cases.auto_touch.SetAutoTouchSensitivityUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AutoTouchSensitivityTest {
    private val sm = KnoxModule.provideKnoxSettingsManager()

    @Test
    fun enableAutoTouchSensitivity() = runTest {
        val setUseCase = SetAutoTouchSensitivityUseCase(sm)
        val getUseCase = GetAutoTouchSensitivityUseCase(sm)
        assert(setUseCase(true) is ApiCall.Success)
        val result = getUseCase.invoke()
        assert(result is ApiCall.Success)
        when(result) {
            is ApiCall.Success -> {
                assert(result.data)
            }
            else -> assert(false)
        }
    }

    @Test
    fun disableAutoTouchSensitivity() = runTest {
        val setUseCase = SetAutoTouchSensitivityUseCase(sm)
        val getUseCase = GetAutoTouchSensitivityUseCase(sm)
        assert(setUseCase(false) is ApiCall.Success)
        val result = getUseCase.invoke()
        assert(result is ApiCall.Success)
        when(result) {
            is ApiCall.Success -> {
                assert(!result.data)
            }
            else -> assert(false)
        }
    }
}
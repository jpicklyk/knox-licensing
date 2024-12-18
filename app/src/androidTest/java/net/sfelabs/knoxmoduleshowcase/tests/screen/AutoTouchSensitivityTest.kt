package net.sfelabs.knoxmoduleshowcase.tests.screen

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.auto_touch.GetAutoTouchSensitivityEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.auto_touch.SetAutoTouchSensitivityEnabledUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 110)
class AutoTouchSensitivityTest {

    @Test
    fun enableAutoTouchSensitivity() = runTest {
        val setUseCase = SetAutoTouchSensitivityEnabledUseCase()
        val getUseCase = GetAutoTouchSensitivityEnabledUseCase()
        assert(setUseCase(true) is ApiResult.Success)
        val result = getUseCase()
        assert(result is ApiResult.Success)
        when(result) {
            is ApiResult.Success -> {
                assertTrue("Error, auto touch sensitivity should be enabled", result.data)
            }
            else -> assert(false)
        }
    }

    @Test
    fun disableAutoTouchSensitivity() = runTest {
        val setUseCase = SetAutoTouchSensitivityEnabledUseCase()
        val getUseCase = GetAutoTouchSensitivityEnabledUseCase()
        assert(setUseCase(false) is ApiResult.Success)
        val result = getUseCase()
        assert(result is ApiResult.Success)
        when(result) {
            is ApiResult.Success -> {
                assertFalse("Error, auto touch sensitivity should be disabled",result.data)
            }
            else -> assert(false)
        }
    }
}
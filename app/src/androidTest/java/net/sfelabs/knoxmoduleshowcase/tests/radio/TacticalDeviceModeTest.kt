package net.sfelabs.knoxmoduleshowcase.tests.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.tdm.GetTacticalDeviceModeEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.tdm.SetTacticalDeviceModeEnabledUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 100)
@SmallTest
class TacticalDeviceModeTest {
    private var currentlyEnabled: Boolean? = null

    @Before
    fun setup() = runTest {
        val result = GetTacticalDeviceModeEnabledUseCase().invoke()
        if(result is ApiResult.Success) {
            currentlyEnabled = result.data
        }
    }
    @Test
    fun enableTacticalDeviceMode() = runTest {
        val res1 = SetTacticalDeviceModeEnabledUseCase().invoke(true)
        assert(res1 is ApiResult.Success)
        val res2 = GetTacticalDeviceModeEnabledUseCase().invoke()
        assert(res2 is ApiResult.Success && res2.data)
    }

    @Test
    fun disableTacticalDeviceMode() = runTest {
        val res1 = SetTacticalDeviceModeEnabledUseCase().invoke(false)
        assert(res1 is ApiResult.Success)
        val res2 = GetTacticalDeviceModeEnabledUseCase().invoke()
        assert(res2 is ApiResult.Success && !res2.data)
    }

    @After
    fun cleanup() = runTest {
        currentlyEnabled?.let { SetTacticalDeviceModeEnabledUseCase().invoke(it) }
    }
}
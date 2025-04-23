package net.sfelabs.knoxmoduleshowcase.tests.screen

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.lockscreen.GetLockscreenTimeoutUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lockscreen.SetLockscreenTimeoutUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 130)
class LockscreenTests {

    @Test
    fun setLockscreenTimeout20s() = runTest {
        val timeout = 20
        val result = SetLockscreenTimeoutUseCase().invoke(timeout)
        assert(result is ApiResult.Success)
        val res2 = GetLockscreenTimeoutUseCase().invoke()
        assert(res2 is ApiResult.Success && res2.data == timeout)
    }

}
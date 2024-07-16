package net.sfelabs.knoxmoduleshowcase.tests.screen

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.annotations.ApiExists
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.lockscreen.GetLockscreenTimeoutUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lockscreen.SetLockscreenTimeoutUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 130)
class LockscreenTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()


    @Test
    fun setLockscreenTimeout20s() = runTest {
        val timeout = 20
        val result = SetLockscreenTimeoutUseCase(systemManager).invoke(timeout)
        assert(result is ApiResult.Success)
        val res2 = GetLockscreenTimeoutUseCase(systemManager).invoke()
        assert(res2 is ApiResult.Success && res2.data == timeout)
    }

}
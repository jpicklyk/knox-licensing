package net.sfelabs.knoxmoduleshowcase.android13

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.lockscreen.GetLockscreenTimeoutUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lockscreen.SetLockscreenTimeoutUseCase
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LockscreenTimeoutTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()

    @Test
    fun getLockscreenTimeout() = runTest {
        val result = GetLockscreenTimeoutUseCase(systemManager).invoke()
        assert(result is ApiCall.Success)
    }

    @Test
    fun setLockscreenTimeout20s() = runTest {
        val result = SetLockscreenTimeoutUseCase(systemManager).invoke(20)
        assert(result is ApiCall.Success)
        val res2 = GetLockscreenTimeoutUseCase(systemManager).invoke()
        assert(res2 is ApiCall.Success && res2.data == 20)
    }

    @After
    fun resetTo10Seconds() = runTest {
        SetLockscreenTimeoutUseCase(systemManager).invoke(10)
    }
}
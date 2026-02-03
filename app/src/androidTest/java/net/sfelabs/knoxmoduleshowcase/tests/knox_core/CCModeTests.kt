package net.sfelabs.knoxmoduleshowcase.tests.knox_core

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_enterprise.domain.model.CCModeState
import net.sfelabs.knox_enterprise.domain.use_cases.GetCCModeUseCase
import net.sfelabs.knox_enterprise.domain.use_cases.SetCCModeUseCase
import org.junit.AfterClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CCModeTests {

    @Test
    fun enableCCMode_andVerifyState() = runTest {
        val setUseCase = SetCCModeUseCase()
        val getUseCase = GetCCModeUseCase()

        val setResult = setUseCase.invoke(true)
        assert(setResult is ApiResult.Success) { "Failed to enable CCMode: $setResult" }

        val getResult = getUseCase.invoke()
        assert(getResult is ApiResult.Success && getResult.data == CCModeState.ENABLED) {
            "CCMode should be ENABLED after setting, but got: $getResult"
        }
    }

    @Test
    fun disableCCMode_andVerifyState() = runTest {
        val setUseCase = SetCCModeUseCase()
        val getUseCase = GetCCModeUseCase()

        val setResult = setUseCase.invoke(false)
        assert(setResult is ApiResult.Success) { "Failed to disable CCMode: $setResult" }

        val getResult = getUseCase.invoke()
        assert(getResult is ApiResult.Success && getResult.data == CCModeState.READY) {
            "CCMode should be READY after disabling, but got: $getResult"
        }
    }

    companion object {
        @JvmStatic
        @AfterClass
        fun cleanup() {
            runBlocking {
                // Ensure CCMode is disabled after tests complete
                SetCCModeUseCase().invoke(false)
            }
        }
    }
}

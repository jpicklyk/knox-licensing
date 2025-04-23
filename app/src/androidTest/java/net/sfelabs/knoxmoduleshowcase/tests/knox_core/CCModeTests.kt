package net.sfelabs.knoxmoduleshowcase.tests.knox_core

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.samsung.android.knox.restriction.AdvancedRestrictionPolicy
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_enterprise.domain.use_cases.GetCCModeUseCase
import net.sfelabs.knox_enterprise.domain.use_cases.SetCCModeUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CCModeTests {


    @Test
    fun checkCCModeState_isDisabled() = runTest {
        val getUseCase = GetCCModeUseCase()
        val result = getUseCase.invoke()
        if(result is ApiResult.Success) {
            println("CCMode state is set to: ${result.data}")
            assert(result.data == AdvancedRestrictionPolicy.CCMODE_STATE_READY)
        } else {
            assert(false)
        }
    }

    @Test
    fun setCCMode_enabled() = runTest {
        val setUseCase = SetCCModeUseCase()

        val setResult = setUseCase.invoke(true)
        assert(setResult is ApiResult.Success)

    }

    @Test
    fun checkCCModeState_isEnabled() = runTest {
        val getUseCase = GetCCModeUseCase()

        val getResult = getUseCase.invoke()
        if(getResult is ApiResult.Success) {
            assert(getResult.data == AdvancedRestrictionPolicy.CCMODE_STATE_ENABLED)
        } else {
            TestCase.assertFalse("Retrieving CCMode state was not successful", true)
        }
    }

    @Test
    fun setCCMode_disabled() = runTest {
        val setUseCase = SetCCModeUseCase()

        val setResult = setUseCase.invoke(false)
        assert(setResult is ApiResult.Success)

    }
}
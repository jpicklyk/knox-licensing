package net.sfelabs.knoxmoduleshowcase.android10

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.samsung.android.knox.EnterpriseKnoxManager
import com.samsung.android.knox.restriction.AdvancedRestrictionPolicy
import junit.framework.TestCase
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_common.domain.use_cases.GetCCModeUseCase
import net.sfelabs.knox_common.domain.use_cases.SetCCModeUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CCModeTests {


    @Test
    fun checkCCModeState_isDisabled() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().context
        val ekm = EnterpriseKnoxManager.getInstance(context)
        val getUseCase = GetCCModeUseCase(ekm)
        val result = getUseCase.invoke()
        if(result is ApiCall.Success) {
            println("CCMode state is set to: ${result.data}")
            assert(result.data == AdvancedRestrictionPolicy.CCMODE_STATE_READY)
        } else {
            assert(false)
        }
    }

    @Test
    fun setCCMode_enabled() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().context
        val ekm = EnterpriseKnoxManager.getInstance(context)

        val setUseCase = SetCCModeUseCase(ekm)

        val setResult = setUseCase.invoke(true)
        assert(setResult is ApiCall.Success)

    }

    @Test
    fun checkCCModeState_isEnabled() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().context
        val ekm = EnterpriseKnoxManager.getInstance(context)
        val getUseCase = GetCCModeUseCase(ekm)

        val getResult = getUseCase.invoke()
        if(getResult is ApiCall.Success) {
            assert(getResult.data == AdvancedRestrictionPolicy.CCMODE_STATE_ENABLED)
        } else {
            TestCase.assertFalse("Retrieving CCMode state was not successful", true)
        }
    }
}
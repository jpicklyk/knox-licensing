package net.sfelabs.knoxmoduleshowcase.croms

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_ngd2.di.KnoxModule
import net.sfelabs.knox_ngd2.domain.use_cases.DisablePogoKeyboardUseCase
import net.sfelabs.knox_ngd2.domain.use_cases.IsPogoKeyboardDisabledUseCase
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PogoKeyboardTests {
    private val sm = KnoxModule.provideKnoxSystemManager()

    @Test
    fun testDisablePogoKeyboard() = runTest {
        val useCase = DisablePogoKeyboardUseCase(sm)
        val result = useCase.invoke(false)
        assert(result is ApiCall.Success)
    }

    @Test
    fun testPogoKeyboardIsDisabled() = runTest {
        val useCase = IsPogoKeyboardDisabledUseCase(sm)
        val result = useCase.invoke()
        if(result is ApiCall.Error) {
            println("POGO FAILED: ${result.uiText}")
        } else if (result is ApiCall.Success){
            println("POGO SUCCESS: ${result.data}")
        }
        assert(result is ApiCall.Success)
    }
}
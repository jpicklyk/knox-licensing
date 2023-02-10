package net.sfelabs.knoxmoduleshowcase

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LcdBacklightStateTest {
    private val sm = KnoxModule.provideKnoxSystemManager()
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun disableLcdBacklightTest() = runTest {

        val setUseCase =
            net.sfelabs.knox_tactical.domain.use_cases.backlight.SetBacklightStateUseCase(
                sm
            )
        val getUseCase =
            net.sfelabs.knox_tactical.domain.use_cases.backlight.GetBacklightStateUseCase(
                sm
            )
        assert(setUseCase(true) is ApiCall.Success)
        val result = getUseCase.invoke()
        assert(result is ApiCall.Success)
        when(result) {
            is ApiCall.Success -> {
                assert(result.data)
            }
            else -> assert(false)
        }
        //allow the screen back on


        setUseCase(true)
    }
}
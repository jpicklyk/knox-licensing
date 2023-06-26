package net.sfelabs.knoxmoduleshowcase.android13

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.ramplus.GetRamPlusDisabledStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ramplus.SetRamPlusStateUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RamPlusTest {
    private val sm = KnoxModule.provideKnoxSystemManager()

    @Test
    @SmallTest
    fun testDefaultDisabled() = runTest {
        val getUseCase = GetRamPlusDisabledStateUseCase(sm)
        val result2 = getUseCase.invoke()
        assert(result2 is ApiCall.Success && result2.data)
    }
    // Setting the state force restarts the device so we can't set and immediately read the state
        @Test
        fun testDisableRamPlus() = runTest {
            val setUseCase = SetRamPlusStateUseCase(sm)
            val getUseCase = GetRamPlusDisabledStateUseCase(sm)

            val result = setUseCase.invoke(true)
            assert(result is ApiCall.Success)

            val result2 = getUseCase.invoke()
            assert(result2 is ApiCall.Success && !result2.data)
        }

        @Test
        fun testEnableRamPlus() = runTest {
            val setUseCase = SetRamPlusStateUseCase(sm)
            val getUseCase = GetRamPlusDisabledStateUseCase(sm)

            val result = setUseCase.invoke(false)
            assert(result is ApiCall.Success)

            val result2 = getUseCase.invoke()
            assert(result2 is ApiCall.Success && result2.data)
        }

}
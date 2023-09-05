package net.sfelabs.knoxmoduleshowcase.android10

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.LteNrModeState
import net.sfelabs.knox_tactical.domain.use_cases.lte.DisableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lte.EnableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lte.Get5gNrModeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lte.GetBandLockingStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lte.Set5gNrModeUseCase
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class KnoxLteTests {
    private val sm = KnoxModule.provideKnoxSystemManager()

    @Test
    fun testNsaMode() = runTest {
        val setCase = Set5gNrModeUseCase(sm).invoke(LteNrModeState.DisableSa)
        assert(setCase is ApiCall.Success)
        val getCase = Get5gNrModeUseCase(sm).invoke()
        assert(getCase is ApiCall.Success && getCase.data == LteNrModeState.DisableSa)
    }

    @Test
    fun testSaMode() = runTest {
        val setCase = Set5gNrModeUseCase(sm).invoke(LteNrModeState.DisableNsa)
        assert(setCase is ApiCall.Success)
        val getCase = Get5gNrModeUseCase(sm).invoke()
        assert(getCase is ApiCall.Success && getCase.data == LteNrModeState.DisableNsa)
    }

    @Test
    fun testSaAndNsaMode() = runTest {
        val setCase = Set5gNrModeUseCase(sm).invoke(LteNrModeState.EnableBothSaAndNsa)
        assert(setCase is ApiCall.Success)
        val getCase = Get5gNrModeUseCase(sm).invoke()
        assert(getCase is ApiCall.Success && getCase.data == LteNrModeState.EnableBothSaAndNsa)
    }

    @Test
    fun enableBandLockN260() = runTest {
        val useCase = EnableBandLockingUseCase(sm)

        val result = useCase.invoke(50000)
        assert(result is ApiCall.Success)

        val useCase2 = GetBandLockingStateUseCase(sm)

        val result2 = useCase2.invoke()
        assert(result2 is ApiCall.Success)
        when(result2) {
            is ApiCall.Success -> {
                assert(result2.data.apiValue == 50000)
            }
            else -> assert(false)
        }
    }


    @Test
    fun disableBandLock() = runTest {
        val useCase = DisableBandLockingUseCase(sm)

        val result = useCase.invoke()
        assert(result is ApiCall.Success)
    }



    /**
     * Put the default LTE mode back on (SA_AND_NSA)
     */
    @After
    fun resetSetting() = runTest {
        disableBandLock()
        testSaAndNsaMode()
    }
}
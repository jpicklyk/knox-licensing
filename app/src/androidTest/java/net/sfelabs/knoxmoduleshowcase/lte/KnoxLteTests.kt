package net.sfelabs.knoxmoduleshowcase.lte

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.LteNrModeState
import net.sfelabs.knox_tactical.domain.use_cases.lte.DisableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lte.EnableBandLockingUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lte.GetBandLockingStateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lte.Get5gNrModeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lte.Set5gNrModeUseCase
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class KnoxLteTests {
    private val sm = KnoxModule.provideKnoxSystemManager()

    @Test
    fun testEnableNSA() = runTest {
        val useCase = Set5gNrModeUseCase(sm)

        val result = useCase.invoke(LteNrModeState.DisableSa)
        assert(result is ApiCall.Success)
    }

    @Test
    fun testConfirmNsaModeEnabled() = runTest {
        val useCase = Get5gNrModeUseCase(sm)

        val result = useCase.invoke()
        assert(result is ApiCall.Success)
        when(result) {
            is ApiCall.Success -> {
                assert(result.data == LteNrModeState.DisableSa)
            } else -> {
            assert(false)
        }
        }
    }

    @Test
    fun testEnableSA() = runTest {
        val useCase = Set5gNrModeUseCase(sm)

        val result = useCase.invoke(LteNrModeState.DisableNsa)
        assert(result is ApiCall.Success)
    }

    @Test
    fun testConfirmSaModeEnabled() = runTest {
        val useCase = Get5gNrModeUseCase(sm)

        val result = useCase.invoke()
        assert(result is ApiCall.Success)
        when(result) {
            is ApiCall.Success -> {
                assert(result.data == LteNrModeState.DisableNsa)
            } else -> {
                assert(false)
            }
        }
    }

    @Test
    fun testEnableBandLockN260() = runTest {
        val useCase = EnableBandLockingUseCase(sm)

        val result = useCase.invoke(50000)
        assert(result is ApiCall.Success)

        val useCase2 = GetBandLockingStateUseCase(sm)

        val result2 = useCase2.invoke()
        assert(result2 is ApiCall.Success)
        when(result2) {
            is ApiCall.Success -> {
                assert(result2.data == 50000)
            }
            else -> assert(false)
        }
    }

   /* @Test
    fun testGetBandLockN260() = runTest {
        val useCase = GetBandLockingStateUseCase(sm)

        val result = useCase.invoke()
        assert(result is ApiCall.Success)
        when(result) {
            is ApiCall.Success -> {
                assert(result.data == 260)
            }
            else -> assert(false)
        }
    }*/

    @Test
    fun testDisableBandLock() = runTest {
        val useCase = DisableBandLockingUseCase(sm)

        val result = useCase.invoke()
        assert(result is ApiCall.Success)
    }

    /**
     * Put the default LTE mode back on (SA_AND_NSA)
     */
    @Test
    fun testEnable5gSaNsa() = runTest {
        val useCase = Set5gNrModeUseCase(sm)

        val result = useCase.invoke(LteNrModeState.EnableBothSaAndNsa)
        assert(result is ApiCall.Success)
    }

    @Test
    fun testConfirmSaAndNsaModeEnabled() = runTest {
        val useCase = Get5gNrModeUseCase(sm)

        val result = useCase.invoke()
        assert(result is ApiCall.Success)
        when(result) {
            is ApiCall.Success -> {
                assert(result.data == LteNrModeState.EnableBothSaAndNsa)
            } else -> {
            assert(false)
        }
        }
    }
}
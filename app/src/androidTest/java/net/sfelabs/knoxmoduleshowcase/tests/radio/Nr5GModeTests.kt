package net.sfelabs.knoxmoduleshowcase.tests.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.LteNrModeState
import net.sfelabs.knox_tactical.domain.use_cases.radio.Get5gNrModeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.radio.Set5gNrModeUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 100)
class Nr5GModeTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private var currentState by Delegates.notNull<Int>()

    @Before
    fun recordCurrentConfiguration() = runTest {
        val result = Get5gNrModeUseCase(systemManager).invoke()
        if (result is ApiResult.Success) {
            currentState = result.data.value
        }
    }

    @Test
    fun setNsaMode() = runTest {
        testSetNrMode(LteNrModeState.DisableSa, null)
    }

    @Test
    fun setNsaModePerSimSlotId0() = runTest {
        testSetNrMode(LteNrModeState.DisableSa, 0)
    }

    @Test
    fun setNsaModePerSimSlotId1() = runTest {
        testSetNrMode(LteNrModeState.DisableSa, 1)
    }

    @Test
    fun setNsaModeSlotId0_and_setSaModeSlotId1() = runTest {
        val setCaseNsa = Set5gNrModeUseCase(systemManager).invoke(LteNrModeState.DisableSa, 0)
        val setCaseSa = Set5gNrModeUseCase(systemManager).invoke(LteNrModeState.DisableNsa, 1)
        assert(setCaseNsa is ApiResult.Success)
        assert(setCaseSa is ApiResult.Success)
        val getCaseNsa = Get5gNrModeUseCase(systemManager).invoke(0)
        val getCaseSa = Get5gNrModeUseCase(systemManager).invoke(1)
        println("Set mode: DisableSa, got mode: ${getCaseNsa.getOrNull()}, simSlotId: 0")
        println("Set mode: DisableNsa, got mode: ${getCaseSa.getOrNull()}, simSlotId: 1")
        assert(getCaseNsa is ApiResult.Success && getCaseNsa.data == LteNrModeState.DisableSa)
        assert(getCaseSa is ApiResult.Success && getCaseSa.data == LteNrModeState.DisableNsa)
    }

    @Test
    fun setSaMode() = runTest {
        testSetNrMode(LteNrModeState.DisableNsa, null)
    }

    @Test
    fun setSaModePerSimSlotId0() = runTest {
        testSetNrMode(LteNrModeState.DisableNsa, 0)
    }

    @Test
    fun setSaAndNsaMode() = runTest {
        testSetNrMode(LteNrModeState.EnableBothSaAndNsa, null)
    }

    @Test
    fun setSaAndNsaModePerSimSlotId0() = runTest {
        testSetNrMode(LteNrModeState.EnableBothSaAndNsa, 0)
    }

    private suspend fun testSetNrMode(mode: LteNrModeState, simSlotId: Int?) {
        val setCase = Set5gNrModeUseCase(systemManager).invoke(mode, simSlotId)
        assert(setCase is ApiResult.Success)
        val getCase = Get5gNrModeUseCase(systemManager).invoke(simSlotId)
        println("Set mode: $mode, got mode: ${getCase.getOrNull()}, simSlotId: $simSlotId")
        assert(getCase is ApiResult.Success && getCase.data == mode)
    }

    @After
    fun cleanup() = runTest {
        Set5gNrModeUseCase(systemManager).invoke(LteNrModeState(currentState))
    }
}
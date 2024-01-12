package net.sfelabs.knoxmoduleshowcase.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
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
        if(result is ApiCall.Success) {
            currentState = result.data.value
        }
    }

    @Test
    fun setNsaMode() = runTest {
        val setCase = Set5gNrModeUseCase(systemManager).invoke(LteNrModeState.DisableSa)
        assert(setCase is ApiCall.Success)
        val getCase = Get5gNrModeUseCase(systemManager).invoke()
        assert(getCase is ApiCall.Success && getCase.data == LteNrModeState.DisableSa)
    }

    @Test
    fun setSaMode() = runTest {
        val setCase = Set5gNrModeUseCase(systemManager).invoke(LteNrModeState.DisableNsa)
        assert(setCase is ApiCall.Success)
        val getCase = Get5gNrModeUseCase(systemManager).invoke()
        assert(getCase is ApiCall.Success && getCase.data == LteNrModeState.DisableNsa)
    }

    @Test
    fun setSaAndNsaMode() = runTest {
        val setCase = Set5gNrModeUseCase(systemManager).invoke(LteNrModeState.EnableBothSaAndNsa)
        assert(setCase is ApiCall.Success)
        val getCase = Get5gNrModeUseCase(systemManager).invoke()
        assert(getCase is ApiCall.Success && getCase.data == LteNrModeState.EnableBothSaAndNsa)
    }

    @After
    fun cleanup() = runTest {
        Set5gNrModeUseCase(systemManager).invoke(LteNrModeState(currentState))
    }


}
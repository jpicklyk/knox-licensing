package net.sfelabs.knoxmoduleshowcase.lte

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.LteNrModeState
import net.sfelabs.knox_tactical.domain.use_cases.lte.Get5gNrModeUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lte.Set5gNrModeUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Private5GTest {
    private val sm = KnoxModule.provideKnoxSystemManager()

    @Test
    fun testGet5GMode() = runTest {
        val useCase = Get5gNrModeUseCase(sm)
        val result = useCase.invoke()
        assert(result is ApiCall.Success)
    }

    @Test
    fun testSA_5GMode() = runTest {
        val useCase = Set5gNrModeUseCase(sm)
        val result = useCase.invoke(LteNrModeState.DisableNsa)
        assert(result is ApiCall.Success)

        val useCase2 = Get5gNrModeUseCase(sm)
        val result2 = useCase2.invoke()
        assert(result2 is ApiCall.Success && result2.data is LteNrModeState.DisableNsa)
    }

    @Test
    fun testNSA_5GMode() = runTest {
        val useCase = Set5gNrModeUseCase(sm)
        val result = useCase.invoke(LteNrModeState.DisableSa)
        assert(result is ApiCall.Success)

        val useCase2 = Get5gNrModeUseCase(sm)
        val result2 = useCase2.invoke()
        assert(result2 is ApiCall.Success && result2.data is LteNrModeState.DisableSa)
    }

    @Test
    fun testBoth_5GMode() = runTest {
        val useCase = Set5gNrModeUseCase(sm)
        val result = useCase.invoke(LteNrModeState.EnableBothSaAndNsa)
        assert(result is ApiCall.Success)

        val useCase2 = Get5gNrModeUseCase(sm)
        val result2 = useCase2.invoke()
        assert(result2 is ApiCall.Success && result2.data is LteNrModeState.EnableBothSaAndNsa)
    }
}
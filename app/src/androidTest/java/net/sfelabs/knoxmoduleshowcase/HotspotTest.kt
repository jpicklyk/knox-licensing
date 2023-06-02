package net.sfelabs.knoxmoduleshowcase

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.LteNrModeState
import net.sfelabs.knox_tactical.domain.use_cases.hotspot.GetHotspot20StateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.hotspot.SetHotspot20StateUseCase
import net.sfelabs.knox_tactical.domain.use_cases.lte.Set5gNrModeUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HotspotTest {
    private val sm = KnoxModule.provideKnoxSettingsManager()
    @Test
    fun testHotspot20Return() = runTest {
        val useCase = GetHotspot20StateUseCase(sm)

        val result = useCase.invoke()
        assert(result is ApiCall.Success)
    }

    @Test
    fun testEnableHotspot20() = runTest {
        val setUseCase = SetHotspot20StateUseCase(sm)
        val getUseCase = GetHotspot20StateUseCase(sm)

        val result = setUseCase.invoke(true)
        assert(result is ApiCall.Success)

        val result2 = getUseCase.invoke()
        assert(result2 is ApiCall.Success && result2.data)
    }

    @Test
    fun testDisableHotspot20() = runTest {
        val setUseCase = SetHotspot20StateUseCase(sm)
        val getUseCase = GetHotspot20StateUseCase(sm)

        val result = setUseCase.invoke(false)
        assert(result is ApiCall.Success)

        val result2 = getUseCase.invoke()
        assert(result2 is ApiCall.Success && !result2.data)
    }
}
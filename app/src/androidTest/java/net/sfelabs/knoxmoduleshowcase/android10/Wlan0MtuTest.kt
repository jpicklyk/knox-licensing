package net.sfelabs.knoxmoduleshowcase.android10

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.wifi.GetWlan0MtuUseCase
import net.sfelabs.knox_tactical.domain.use_cases.wifi.SetWlan0MtuUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Wlan0MtuTest {
    private val sm = KnoxModule.provideKnoxSystemManager()

    @Test
    fun testGetWlan0Mtu() = runTest {
        val useCase = GetWlan0MtuUseCase(sm)
        val result = useCase.invoke()
        assert(result is ApiCall.Success)
    }

    @Test
    fun testSetWlan0Mtu() = runTest {
        val useCase = SetWlan0MtuUseCase(sm)
        val result = useCase.invoke(1430)
        assert(result is ApiCall.Success)

        val useCase2 = GetWlan0MtuUseCase(sm)
        val result2 = useCase2.invoke()
        assert(result2 is ApiCall.Success && result2.data == 1430)
    }

}
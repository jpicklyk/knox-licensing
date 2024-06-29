package net.sfelabs.knoxmoduleshowcase.wifi

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.wifi.GetWlan0MtuUseCase
import net.sfelabs.knox_tactical.domain.use_cases.wifi.SetWlan0MtuUseCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 100)
class Wlan0MtuTest {
    private val sm = KnoxModule.provideKnoxSystemManager()

    @Test
    fun testSetWlan0Mtu() = runTest {
        val useCase = SetWlan0MtuUseCase(sm)
        val result = useCase.invoke(1430)
        assert(result is ApiResult.Success)

        val useCase2 = GetWlan0MtuUseCase(sm)
        val result2 = useCase2.invoke()
        assert(result2 is ApiResult.Success && result2.data == 1430)
    }

}
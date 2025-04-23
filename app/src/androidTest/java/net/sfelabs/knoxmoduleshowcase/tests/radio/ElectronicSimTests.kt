package net.sfelabs.knoxmoduleshowcase.tests.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.use_cases.sim.GetElectronicSimEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.sim.SetElectronicSimEnabledUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.properties.Delegates

@SmallTest
@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 131)
class ElectronicSimTests {
    private var esimEnabled by Delegates.notNull<Boolean>()

    @Before
    fun readCurrentState() = runTest {
        val result = GetElectronicSimEnabledUseCase().invoke()
        if(result is ApiResult.Success) {
            esimEnabled = result.data
        }
    }

    @Test
    fun disableElectronicSim() = runTest {
        val result = SetElectronicSimEnabledUseCase().invoke(false)
        assert(result is ApiResult.Success)

        val result2 = GetElectronicSimEnabledUseCase().invoke()
        assert(result2 is ApiResult.Success && !result2.data)
    }

    @Test
    fun enableElectronicSim() = runTest {
        val result = SetElectronicSimEnabledUseCase().invoke(true)
        assert(result is ApiResult.Success)

        val result2 = GetElectronicSimEnabledUseCase().invoke()
        assert(result2 is ApiResult.Success && result2.data)
    }

    @After
    fun resetElectronicSimSettings() = runTest {
        SetElectronicSimEnabledUseCase().invoke(esimEnabled)
    }
}
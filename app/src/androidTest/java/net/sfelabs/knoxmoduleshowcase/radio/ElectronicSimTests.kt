package net.sfelabs.knoxmoduleshowcase.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
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
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val settingsManager = KnoxModule.provideKnoxSettingsManager()
    private var esimEnabled by Delegates.notNull<Boolean>()

    @Before
    fun readCurrentState() = runTest {
        val result = GetElectronicSimEnabledUseCase(settingsManager).invoke()
        if(result is ApiResult.Success) {
            esimEnabled = result.data.value
        }
    }

    @Test
    fun disableElectronicSim() = runTest {
        val result = SetElectronicSimEnabledUseCase(settingsManager).invoke(false)
        assert(result is ApiResult.Success)

        val result2 = GetElectronicSimEnabledUseCase(settingsManager).invoke()
        assert(result2 is ApiResult.Success && !result2.data.value)
    }

    @Test
    fun enableElectronicSim() = runTest {
        val result = SetElectronicSimEnabledUseCase(settingsManager).invoke(true)
        assert(result is ApiResult.Success)

        val result2 = GetElectronicSimEnabledUseCase(settingsManager).invoke()
        assert(result2 is ApiResult.Success && result2.data.value)
    }

    @After
    fun resetElectronicSimSettings() = runTest {
        SetElectronicSimEnabledUseCase(settingsManager).invoke(esimEnabled)
    }
}
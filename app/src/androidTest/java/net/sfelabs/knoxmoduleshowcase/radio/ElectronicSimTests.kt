package net.sfelabs.knoxmoduleshowcase.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.sim.GetElectronicSimEnabledUseCase
import net.sfelabs.knox_tactical.domain.use_cases.sim.SetElectronicSimEnabledUseCase
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
        if(result is ApiCall.Success) {
            esimEnabled = result.data.apiValue
        }
    }

    @Test
    fun disableElectronicSim() = runTest {
        val result = SetElectronicSimEnabledUseCase(settingsManager).invoke(false)
        assert(result is ApiCall.Success)

        val result2 = GetElectronicSimEnabledUseCase(settingsManager).invoke()
        assert(result2 is ApiCall.Success && !result2.data.apiValue)
    }

    @Test
    fun enableElectronicSim() = runTest {
        val result = SetElectronicSimEnabledUseCase(settingsManager).invoke(true)
        assert(result is ApiCall.Success)

        val result2 = GetElectronicSimEnabledUseCase(settingsManager).invoke()
        assert(result2 is ApiCall.Success && result2.data.apiValue)
    }


}
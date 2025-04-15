package net.sfelabs.knox_tactical.domain.use_cases

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.annotations.ApiExists
import net.sfelabs.core.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@SmallTest
@ApiExists
class RamPlusTest {
    private val systemManager = KnoxModule.provideKnoxSystemManager()

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130, excludeModels = ["SM-X308U"])
    fun getRamPlusDisableState_Exists() = runTest {
        val kClass = systemManager::class
        assert(checkMethodExistence(kClass, "getRamPlusDisableState"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130, excludeModels = ["SM-X308U"])
    fun setRamPlusDisableState_Exists() = runTest {
        val kClass = systemManager::class
        assert(checkMethodExistence(kClass, "setRamPlusDisableState"))
    }

/*
// Setting the state force restarts the device so we can't set and immediately read the state
    @Test
    fun testDisableRamPlus() = runTest {
        val setUseCase = SetRamPlusStateUseCase(systemManager)
        val getUseCase = GetRamPlusDisabledStateUseCase(systemManager)

        val result = setUseCase.invoke(true)
        assert(result is ApiCall.Success)

        val result2 = getUseCase.invoke()
        assert(result2 is ApiCall.Success && !result2.data.enabled)
    }

    @Test
    fun testEnableRamPlus() = runTest {
        val setUseCase = SetRamPlusStateUseCase(systemManager)
        val getUseCase = GetRamPlusDisabledStateUseCase(systemManager)

        val result = setUseCase.invoke(false)
        assert(result is ApiCall.Success)

        val result2 = getUseCase.invoke()
        assert(result2 is ApiCall.Success && result2.data.enabled)
    }
*/
}
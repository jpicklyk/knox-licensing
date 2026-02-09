package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.CustomDeviceManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

@SmallTest
@ApiExists
class RamPlusTest {
    private val systemManager = CustomDeviceManager.getInstance().systemManager

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
}

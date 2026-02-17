package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

/**
 * Knox Tactical SDK API existence tests for APIs introduced in release version 132.
 *
 * When adding a new API from this release version, add a test method here.
 */
@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 132)
class ApiExistenceV132Tests {

    // region SystemManager

    @Test
    fun getNightVisionModeState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getNightVisionModeState")) { "Expected method 'getNightVisionModeState' to exist on SystemManager" }
    }

    @Test
    fun setNightVisionModeState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setNightVisionModeState")) { "Expected method 'setNightVisionModeState' to exist on SystemManager" }
    }

    @Test
    fun enableLteBandLockingPerSimSlot_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "enableLteBandLockingPerSimSlot")) { "Expected method 'enableLteBandLockingPerSimSlot' to exist on SystemManager" }
    }

    @Test
    fun disableLteBandLockingPerSimSlot_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "disableLteBandLockingPerSimSlot")) { "Expected method 'disableLteBandLockingPerSimSlot' to exist on SystemManager" }
    }

    @Test
    fun enable5GBandLockingPerSimSlot_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "enable5GBandLockingPerSimSlot")) { "Expected method 'enable5GBandLockingPerSimSlot' to exist on SystemManager" }
    }

    @Test
    fun disable5GBandLockingPerSimSlot_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "disable5GBandLockingPerSimSlot")) { "Expected method 'disable5GBandLockingPerSimSlot' to exist on SystemManager" }
    }

    // endregion
}

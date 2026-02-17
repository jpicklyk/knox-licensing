package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SystemManager
import com.samsung.android.knox.hdm.HdmManager
import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

/**
 * Knox Tactical SDK API existence tests for APIs introduced in release version 130.
 *
 * When adding a new API from this release version, add a test method here.
 */
@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 130)
class ApiExistenceV130Tests {

    // region SystemManager

    @Test
    fun getActivityTime_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getActivityTime")) { "Expected method 'getActivityTime' to exist on SystemManager" }
    }

    @Test
    fun setActivityTime_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setActivityTime")) { "Expected method 'setActivityTime' to exist on SystemManager" }
    }

    @Test
    fun getUsbDeviceAccessAllowedListSerialNumber_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getUsbDeviceAccessAllowedListSerialNumber")) { "Expected method 'getUsbDeviceAccessAllowedListSerialNumber' to exist on SystemManager" }
    }

    @Test
    fun setUsbDeviceAccessAllowedListSerialNumber_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setUsbDeviceAccessAllowedListSerialNumber")) { "Expected method 'setUsbDeviceAccessAllowedListSerialNumber' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-X308U"])
    fun getRamPlusDisableState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getRamPlusDisableState")) { "Expected method 'getRamPlusDisableState' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-X308U"])
    fun setRamPlusDisableState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setRamPlusDisableState")) { "Expected method 'setRamPlusDisableState' to exist on SystemManager" }
    }

    // endregion

    // region RestrictionPolicy

    @Test
    fun isRandomisedMacAddressEnabled_Exists() = runTest {
        assert(checkMethodExistence(RestrictionPolicy::class, "isRandomisedMacAddressEnabled")) { "Expected method 'isRandomisedMacAddressEnabled' to exist on RestrictionPolicy" }
    }

    @Test
    fun enableRandomisedMacAddress_Exists() = runTest {
        assert(checkMethodExistence(RestrictionPolicy::class, "enableRandomisedMacAddress")) { "Expected method 'enableRandomisedMacAddress' to exist on RestrictionPolicy" }
    }

    // endregion

    // region HdmManager

    @Test
    fun getHdmPolicy_Exists() = runTest {
        assert(checkMethodExistence(HdmManager::class, "getHdmPolicy")) { "Expected method 'getHdmPolicy' to exist on HdmManager" }
    }

    /**
     * stealthCpHwControl was deprecated in version 131 and replaced by stealthHwControl.
     * This test only runs on exactly version 130 devices.
     */
    @Test
    @TacticalSdkSuppress(maxReleaseVersion = 130)
    fun stealthCpHwControl_Exists() = runTest {
        assert(checkMethodExistence(HdmManager::class, "stealthCpHwControl")) { "Expected method 'stealthCpHwControl' to exist on HdmManager" }
    }

    // endregion
}

package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.application.ApplicationPolicy
import com.samsung.android.knox.custom.SettingsManager
import com.samsung.android.knox.custom.SystemManager
import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

/**
 * Knox Tactical SDK API existence tests for APIs introduced in release version 100.
 *
 * When adding a new API from this release version, add a test method here.
 */
@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 100)
class ApiExistenceV100Tests {

    // region SystemManager

    @Test
    fun getLcdBacklightState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getLcdBacklightState")) { "Expected method 'getLcdBacklightState' to exist on SystemManager" }
    }

    @Test
    fun setLcdBacklightState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setLcdBacklightState")) { "Expected method 'setLcdBacklightState' to exist on SystemManager" }
    }

    @Test
    fun get5gNrModeState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "get5gNrModeState")) { "Expected method 'get5gNrModeState' to exist on SystemManager" }
    }

    @Test
    fun set5gNrModeState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "set5gNrModeState")) { "Expected method 'set5gNrModeState' to exist on SystemManager" }
    }

    @Test
    fun get5gNrModeStatePerSimSlot_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "get5gNrModeStatePerSimSlot")) { "Expected method 'get5gNrModeStatePerSimSlot' to exist on SystemManager" }
    }

    @Test
    fun set5gNrModeStatePerSimSlot_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "set5gNrModeStatePerSimSlot")) { "Expected method 'set5gNrModeStatePerSimSlot' to exist on SystemManager" }
    }

    @Test
    fun getLteBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getLteBandLocking")) { "Expected method 'getLteBandLocking' to exist on SystemManager" }
    }

    @Test
    fun getLteBandLockingPerSimSlot_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getLteBandLockingPerSimSlot")) { "Expected method 'getLteBandLockingPerSimSlot' to exist on SystemManager" }
    }

    @Test
    fun enableLteBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "enableLteBandLocking")) { "Expected method 'enableLteBandLocking' to exist on SystemManager" }
    }

    @Test
    fun disableLteBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "disableLteBandLocking")) { "Expected method 'disableLteBandLocking' to exist on SystemManager" }
    }

    @Test
    fun enableTcpDump_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "enableTcpDump")) { "Expected method 'enableTcpDump' to exist on SystemManager" }
    }

    @Test
    fun disableTcpDump_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "disableTcpDump")) { "Expected method 'disableTcpDump' to exist on SystemManager" }
    }

    @Test
    fun isTcpDumpEnabled_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "isTcpDumpEnabled")) { "Expected method 'isTcpDumpEnabled' to exist on SystemManager" }
    }

    @Test
    fun getUsbConnectionType_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getUsbConnectionType")) { "Expected method 'getUsbConnectionType' to exist on SystemManager" }
    }

    @Test
    fun getKnoxWlanZeroMtu_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getKnoxWlanZeroMtu")) { "Expected method 'getKnoxWlanZeroMtu' to exist on SystemManager" }
    }

    @Test
    fun setWlanZeroMtu_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setWlanZeroMtu")) { "Expected method 'setWlanZeroMtu' to exist on SystemManager" }
    }

    @Test
    fun setEthernetConfigurations_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setEthernetConfigurations")) { "Expected method 'setEthernetConfigurations' to exist on SystemManager" }
    }

    @Test
    fun setEthernetConfigurationsMultiDns_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setEthernetConfigurationsMultiDns")) { "Expected method 'setEthernetConfigurationsMultiDns' to exist on SystemManager" }
    }

    // endregion

    // region SettingsManager

    @Test
    fun getEthernetAutoConnectionState_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "getEthernetAutoConnectionState")) { "Expected method 'getEthernetAutoConnectionState' to exist on SettingsManager" }
    }

    // endregion

    // region RestrictionPolicy

    @Test
    fun isTacticalDeviceModeEnabled_Exists() = runTest {
        assert(checkMethodExistence(RestrictionPolicy::class, "isTacticalDeviceModeEnabled")) { "Expected method 'isTacticalDeviceModeEnabled' to exist on RestrictionPolicy" }
    }

    @Test
    fun enableTacticalDeviceMode_Exists() = runTest {
        assert(checkMethodExistence(RestrictionPolicy::class, "enableTacticalDeviceMode")) { "Expected method 'enableTacticalDeviceMode' to exist on RestrictionPolicy" }
    }

    @Test
    fun allowUsbHostStorage_Exists() = runTest {
        assert(checkMethodExistence(RestrictionPolicy::class, "allowUsbHostStorage")) { "Expected method 'allowUsbHostStorage' to exist on RestrictionPolicy" }
    }

    // endregion

    // region ApplicationPolicy

    @Test
    fun getPackagesFromUsbHostWhiteList_Exists() = runTest {
        assert(checkMethodExistence(ApplicationPolicy::class, "getPackagesFromUsbHostWhiteList")) { "Expected method 'getPackagesFromUsbHostWhiteList' to exist on ApplicationPolicy" }
    }

    @Test
    fun addPackageToUsbHostWhiteList_Exists() = runTest {
        assert(checkMethodExistence(ApplicationPolicy::class, "addPackageToUsbHostWhiteList")) { "Expected method 'addPackageToUsbHostWhiteList' to exist on ApplicationPolicy" }
    }

    @Test
    fun removePackageFromUsbHostWhiteList_Exists() = runTest {
        assert(checkMethodExistence(ApplicationPolicy::class, "removePackageFromUsbHostWhiteList")) { "Expected method 'removePackageFromUsbHostWhiteList' to exist on ApplicationPolicy" }
    }

    // endregion
}

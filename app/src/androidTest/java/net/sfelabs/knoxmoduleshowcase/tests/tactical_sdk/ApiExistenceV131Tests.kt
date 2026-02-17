package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SettingsManager
import com.samsung.android.knox.custom.SystemManager
import com.samsung.android.knox.hdm.HdmManager
import com.samsung.android.knox.restriction.PhoneRestrictionPolicy
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

/**
 * Knox Tactical SDK API existence tests for APIs introduced in release version 131.
 *
 * When adding a new API from this release version, add a test method here.
 */
@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 131)
class ApiExistenceV131Tests {

    // region SystemManager

    @Test
    fun getMacAddressForEthernetInterface_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getMacAddressForEthernetInterface")) { "Expected method 'getMacAddressForEthernetInterface' to exist on SystemManager" }
    }

    @Test
    fun getEthernetInterfaceNameForMacAddress_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getEthernetInterfaceNameForMacAddress")) { "Expected method 'getEthernetInterfaceNameForMacAddress' to exist on SystemManager" }
    }

    @Test
    fun get5GBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "get5GBandLocking")) { "Expected method 'get5GBandLocking' to exist on SystemManager" }
    }

    @Test
    fun enable5GBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "enable5GBandLocking")) { "Expected method 'enable5GBandLocking' to exist on SystemManager" }
    }

    @Test
    fun disable5GBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "disable5GBandLocking")) { "Expected method 'disable5GBandLocking' to exist on SystemManager" }
    }

    @Test
    fun get5GBandLockingPerSimSlot_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "get5GBandLockingPerSimSlot")) { "Expected method 'get5GBandLockingPerSimSlot' to exist on SystemManager" }
    }

    @Test
    fun get2GConnectivityState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "get2GConnectivityState")) { "Expected method 'get2GConnectivityState' to exist on SystemManager" }
    }

    @Test
    fun set2GConnectivityState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "set2GConnectivityState")) { "Expected method 'set2GConnectivityState' to exist on SystemManager" }
    }

    // endregion

    // region SettingsManager

    @Test
    fun addIpAddressToEthernetInterface_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "addIpAddressToEthernetInterface")) { "Expected method 'addIpAddressToEthernetInterface' to exist on SettingsManager" }
    }

    @Test
    fun deleteIpAddressToEthernetInterface_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "deleteIpAddressToEthernetInterface")) { "Expected method 'deleteIpAddressToEthernetInterface' to exist on SettingsManager" }
    }

    @Test
    fun listIpAddress_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "listIpAddress")) { "Expected method 'listIpAddress' to exist on SettingsManager" }
    }

    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-X308U"])
    fun getExtraBrightness_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "getExtraBrightness")) { "Expected method 'getExtraBrightness' to exist on SettingsManager" }
    }

    @Test
    @TacticalSdkSuppress(excludeModels = ["SM-X308U"])
    fun setExtraBrightness_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "setExtraBrightness")) { "Expected method 'setExtraBrightness' to exist on SettingsManager" }
    }

    @Test
    fun getEsimEnabled_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "getEsimEnabled")) { "Expected method 'getEsimEnabled' to exist on SettingsManager" }
    }

    @Test
    fun setEsimEnabled_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "setEsimEnabled")) { "Expected method 'setEsimEnabled' to exist on SettingsManager" }
    }

    // endregion

    // region PhoneRestrictionPolicy

    @Test
    fun isIMSEnabled_Exists() = runTest {
        assert(checkMethodExistence(PhoneRestrictionPolicy::class, "isIMSEnabled")) { "Expected method 'isIMSEnabled' to exist on PhoneRestrictionPolicy" }
    }

    @Test
    fun setIMSEnabled_Exists() = runTest {
        assert(checkMethodExistence(PhoneRestrictionPolicy::class, "setIMSEnabled")) { "Expected method 'setIMSEnabled' to exist on PhoneRestrictionPolicy" }
    }

    // endregion

    // region HdmManager

    @Test
    fun stealthHwControl_Exists() = runTest {
        assert(checkMethodExistence(HdmManager::class, "stealthHwControl")) { "Expected method 'stealthHwControl' to exist on HdmManager" }
    }

    // endregion
}

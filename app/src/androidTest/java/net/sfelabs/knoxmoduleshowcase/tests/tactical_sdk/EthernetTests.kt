package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SettingsManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 100)
class EthernetTests {
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun setEthernetConfigurations_Exists() {
        assert(checkMethodExistence(SystemManager::class, "setEthernetConfigurations")) { "Expected method 'setEthernetConfigurations' to exist on SystemManager" }
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun setEthernetConfigurationsMultiDns_Exists() {
        assert(checkMethodExistence(SystemManager::class, "setEthernetConfigurationsMultiDns")) { "Expected method 'setEthernetConfigurationsMultiDns' to exist on SystemManager" }
    }

    @Test
    fun setEthernetAutoConnectionState_Exists() {
        assert(checkMethodExistence(SettingsManager::class, "setEthernetAutoConnectionState")) { "Expected method 'setEthernetAutoConnectionState' to exist on SettingsManager" }
    }

    @Test
    fun getEthernetAutoConnectionState_Exists() {
        assert(checkMethodExistence(SettingsManager::class, "getEthernetAutoConnectionState")) { "Expected method 'getEthernetAutoConnectionState' to exist on SettingsManager" }
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 112)
    fun getMacAddressForEthernetInterface_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getMacAddressForEthernetInterface")) { "Expected method 'getMacAddressForEthernetInterface' to exist on SystemManager" }
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 112)
    fun getEthernetInterfaceNameForMacAddress_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getEthernetInterfaceNameForMacAddress")) { "Expected method 'getEthernetInterfaceNameForMacAddress' to exist on SystemManager" }
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun addIpAddressToEthernetInterface_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "addIpAddressToEthernetInterface")) { "Expected method 'addIpAddressToEthernetInterface' to exist on SettingsManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun deleteIpAddressToEthernetInterface_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "deleteIpAddressToEthernetInterface")) { "Expected method 'deleteIpAddressToEthernetInterface' to exist on SettingsManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun listIpAddress_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "listIpAddress")) { "Expected method 'listIpAddress' to exist on SettingsManager" }
    }
}

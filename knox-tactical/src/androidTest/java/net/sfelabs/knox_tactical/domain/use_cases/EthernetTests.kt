package net.sfelabs.knox_tactical.domain.use_cases

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SettingsManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.annotations.ApiExists
import net.sfelabs.core.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 100)
class EthernetTests {
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun setEthernetConfigurations_Exists() {
        assert(checkMethodExistence(SystemManager::class, "setEthernetConfigurations"))
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun setEthernetConfigurationsMultiDns_Exists() {
        assert(checkMethodExistence(SystemManager::class, "setEthernetConfigurationsMultiDns"))
    }

    @Test
    fun setEthernetAutoConnectionState_Exists() {
        assert(checkMethodExistence(SettingsManager::class, "setEthernetAutoConnectionState"))
    }

    @Test
    fun getEthernetAutoConnectionState_Exists() {
        assert(checkMethodExistence(SettingsManager::class, "getEthernetAutoConnectionState"))
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 112)
    fun getMacAddressForEthernetInterface_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getMacAddressForEthernetInterface"))
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 112)
    fun getEthernetInterfaceNameForMacAddress_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getEthernetInterfaceNameForMacAddress"))
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun addIpAddressToEthernetInterface_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class, "addIpAddressToEthernetInterface"))
    }
}
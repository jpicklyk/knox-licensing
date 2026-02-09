package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.application.ApplicationPolicy
import com.samsung.android.knox.custom.SystemManager
import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 100)
class UsbTests {
    @Test
    fun getUsbConnectionType_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getUsbConnectionType")) { "Expected method 'getUsbConnectionType' to exist on SystemManager" }
    }

    @Test
    fun setUsbConnectionType_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"setUsbConnectionType")) { "Expected method 'setUsbConnectionType' to exist on SystemManager" }
    }

    @Test
    fun getUsbDeviceAccessAllowedList_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getUsbDeviceAccessAllowedList")) { "Expected method 'getUsbDeviceAccessAllowedList' to exist on SystemManager" }
    }

    @Test
    fun setUsbDeviceAccessAllowedList_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"setUsbDeviceAccessAllowedList")) { "Expected method 'setUsbDeviceAccessAllowedList' to exist on SystemManager" }
    }

    @Test
    fun allowUsbHostStorage_Exists() = runTest {
        assert(checkMethodExistence(RestrictionPolicy::class,"allowUsbHostStorage")) { "Expected method 'allowUsbHostStorage' to exist on RestrictionPolicy" }
    }

    @Test
    fun addPackageToUsbHostWhiteList_Exists() = runTest {
        assert(checkMethodExistence(ApplicationPolicy::class,"addPackageToUsbHostWhiteList")) { "Expected method 'addPackageToUsbHostWhiteList' to exist on ApplicationPolicy" }
    }

    @Test
    fun removePackageFromUsbHostWhiteList_Exists() = runTest {
        assert(checkMethodExistence(ApplicationPolicy::class,"removePackageFromUsbHostWhiteList")) { "Expected method 'removePackageFromUsbHostWhiteList' to exist on ApplicationPolicy" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun getMacAddressForEthernetInterface_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getMacAddressForEthernetInterface")) { "Expected method 'getMacAddressForEthernetInterface' to exist on SystemManager" }
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun getEthernetInterfaceNameForMacAddress_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getEthernetInterfaceNameForMacAddress")) { "Expected method 'getEthernetInterfaceNameForMacAddress' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun stopPPPD_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"stopPPPD")) { "Expected method 'stopPPPD' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun setUsbDeviceAccessAllowedListSerialNumber_Exists() = runTest {
        assert(checkMethodExistence(
                SystemManager::class,
                "setUsbDeviceAccessAllowedListSerialNumber"
            )
        ) { "Expected method 'setUsbDeviceAccessAllowedListSerialNumber' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun getUsbDeviceAccessAllowedListSerialNumber_Exists() = runTest {
        assert(checkMethodExistence(
            SystemManager::class,
                "getUsbDeviceAccessAllowedListSerialNumber"
            )
        ) { "Expected method 'getUsbDeviceAccessAllowedListSerialNumber' to exist on SystemManager" }
    }

    @Test
    fun getPackagesFromUsbHostWhiteList_Exists() = runTest {
        assert(checkMethodExistence(ApplicationPolicy::class,"getPackagesFromUsbHostWhiteList")) { "Expected method 'getPackagesFromUsbHostWhiteList' to exist on ApplicationPolicy" }
    }
}

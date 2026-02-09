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
        assert(checkMethodExistence(SystemManager::class,"getUsbConnectionType"))
    }

    @Test
    fun setUsbConnectionType_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"setUsbConnectionType"))
    }

    @Test
    fun getUsbDeviceAccessAllowedList_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getUsbDeviceAccessAllowedList"))
    }

    @Test
    fun setUsbDeviceAccessAllowedList_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"setUsbDeviceAccessAllowedList"))
    }

    @Test
    fun allowUsbHostStorage_Exists() = runTest {
        assert(checkMethodExistence(RestrictionPolicy::class,"allowUsbHostStorage"))
    }

    @Test
    fun addPackageToUsbHostWhiteList_Exists() = runTest {
        assert(checkMethodExistence(ApplicationPolicy::class,"addPackageToUsbHostWhiteList"))
    }

    @Test
    fun removePackageFromUsbHostWhiteList_Exists() = runTest {
        assert(checkMethodExistence(ApplicationPolicy::class,"removePackageFromUsbHostWhiteList"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun getMacAddressForEthernetInterface_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getMacAddressForEthernetInterface"))
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun getEthernetInterfaceNameForMacAddress_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getEthernetInterfaceNameForMacAddress"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun stopPppd_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"stopPPPD"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun setUsbDeviceAccessAllowedListSerialNumber_Exists() = runTest {
        assert(checkMethodExistence(
                SystemManager::class,
                "setUsbDeviceAccessAllowedListSerialNumber"
            )
        )
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun getUsbDeviceAccessAllowedListSerialNumber_Exists() = runTest {
        assert(checkMethodExistence(
            SystemManager::class,
                "getUsbDeviceAccessAllowedListSerialNumber"
            )
        )
    }

    @Test
    fun getPackagesFromUsbHostWhiteList_Exists() = runTest {
        assert(checkMethodExistence(ApplicationPolicy::class,"getPackagesFromUsbHostWhiteList"))
    }
}

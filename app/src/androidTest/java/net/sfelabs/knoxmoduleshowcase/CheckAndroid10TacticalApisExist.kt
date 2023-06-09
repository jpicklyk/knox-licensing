package net.sfelabs.knoxmoduleshowcase

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.checkMethodExistence
import net.sfelabs.knox_tactical.di.KnoxModule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class CheckAndroid10TacticalApisExist {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private val settingsManager = KnoxModule.provideKnoxSettingsManager()
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val edm = KnoxModule.provideKnoxEnterpriseDeviceManager(appContext)
    private val restrictionPolicy = KnoxModule.provideKnoxRestrictionPolicy(edm)
    private val applicationPolicy = KnoxModule.provideKnoxApplicationPolicy(edm)

    /** Tactical Device Mode APIs **/
    @Test
    fun enableTacticalDeviceMode_Exists() {
        assert(checkMethodExistence(restrictionPolicy::class, "enableTacticalDeviceMode"))
    }
    @Test
    fun isTacticalDeviceModeEnabled_Exists() {
        assert(checkMethodExistence(restrictionPolicy::class, "isTacticalDeviceModeEnabled"))
    }
    /** Ethernet APIs **/
    @Test
    fun setEthernetConfigurations_Exists() {
        assert(checkMethodExistence(systemManager::class, "setEthernetConfigurations"))
    }
    @Test
    fun setEthernetConfigurationsMultiDns_Exists() {
        assert(checkMethodExistence(systemManager::class, "setEthernetConfigurationsMultiDns"))
    }

    @Test
    fun setEthernetAutoConnectionState_Exists() {
        assert(checkMethodExistence(settingsManager::class, "setEthernetAutoConnectionState"))
    }

    @Test
    fun getEthernetAutoConnectionState_Exists() {
        assert(checkMethodExistence(settingsManager::class, "getEthernetAutoConnectionState"))
    }

    /** LTE Related APIs **/
    @Test
    fun get5gNrModeState_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"get5gNrModeState"))
    }

    @Test
    fun set5gNrModeState_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"get5gNrModeState"))
    }

    @Test
    fun getLteBandLocking_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"getLteBandLocking"))
    }

    @Test
    fun enableLteBandLocking_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"enableLteBandLocking"))
    }

    @Test
    fun disableLteBandLocking_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"disableLteBandLocking"))
    }

    /** USB Related APIs **/
    @Test
    fun getUsbConnectionType_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"getUsbConnectionType"))
    }

    @Test
    fun setUsbConnectionType_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"setUsbConnectionType"))
    }

    @Test
    fun getUsbDeviceAccessAllowedList_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"getUsbDeviceAccessAllowedList"))
    }

    @Test
    fun setUsbDeviceAccessAllowedList_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"setUsbDeviceAccessAllowedList"))
    }

    @Test
    fun allowUsbHostStorage_Exists() = runTest {
        assert(checkMethodExistence(restrictionPolicy::class,"allowUsbHostStorage"))
    }

    @Test
    fun addPackageToUsbHostWhiteList_Exists() = runTest {
        assert(checkMethodExistence(applicationPolicy::class,"addPackageToUsbHostWhiteList"))
    }

    @Test
    fun removePackageFromUsbHostWhiteList_Exists() = runTest {
        assert(checkMethodExistence(applicationPolicy::class,"removePackageFromUsbHostWhiteList"))
    }

    /** Wi-Fi Related APIs **/
    @Test
    fun getKnoxWlanZeroMtu_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"getKnoxWlanZeroMtu"))
    }
    @Test
    fun setWlanZeroMtu_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"setWlanZeroMtu"))
    }
    @Test
    fun getHotspot20State_Exists() = runTest {
        assert(checkMethodExistence(settingsManager::class,"getHotspot20State"))
    }
    @Test
    fun setHotspot20State_Exists() = runTest {
        assert(checkMethodExistence(settingsManager::class,"setHotspot20State"))
    }

    /** Backlight Related APIs **/
    @Test
    fun getLcdBacklightState_Exists() = runTest {
        val kClass = systemManager::class
        assert(checkMethodExistence(kClass, "getLcdBacklightState"))
    }

    @Test
    fun setLcdBacklightState_Exists() = runTest {
        val kClass = systemManager::class
        assert(checkMethodExistence(kClass, "setLcdBacklightState"))
    }

    /** TCP Dump Related APIs **/
    @Test
    fun isTcpDumpEnabled_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"isTcpDumpEnabled"))
    }

    @Test
    fun enableTcpDump_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"enableTcpDump"))
    }

    @Test
    fun disableTcpDump_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"disableTcpDump"))
    }
}
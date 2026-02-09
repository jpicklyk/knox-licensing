package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SettingsManager
import com.samsung.android.knox.custom.SystemManager
import com.samsung.android.knox.restriction.PhoneRestrictionPolicy
import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 100)
class RadioTests {
    @Test
    fun enableTacticalDeviceMode_Exists() {
        assert(checkMethodExistence(RestrictionPolicy::class, "enableTacticalDeviceMode")) { "Expected method 'enableTacticalDeviceMode' to exist on RestrictionPolicy" }
    }
    @Test
    fun isTacticalDeviceModeEnabled_Exists() {
        assert(checkMethodExistence(RestrictionPolicy::class, "isTacticalDeviceModeEnabled")) { "Expected method 'isTacticalDeviceModeEnabled' to exist on RestrictionPolicy" }
    }
    @Test
    fun get5gNrModeState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"get5gNrModeState")) { "Expected method 'get5gNrModeState' to exist on SystemManager" }
    }

    @Test
    fun set5gNrModeState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"set5gNrModeState")) { "Expected method 'set5gNrModeState' to exist on SystemManager" }
    }

    @Test
    fun getLteBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getLteBandLocking")) { "Expected method 'getLteBandLocking' to exist on SystemManager" }
    }

    @Test
    fun enableLteBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"enableLteBandLocking")) { "Expected method 'enableLteBandLocking' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun disableLteBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"disableLteBandLocking")) { "Expected method 'disableLteBandLocking' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun get5GBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"get5GBandLocking")) { "Expected method 'get5GBandLocking' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun enable5GBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"enable5GBandLocking")) { "Expected method 'enable5GBandLocking' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun disable5GBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"disable5GBandLocking")) { "Expected method 'disable5GBandLocking' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun set2GConnectivityState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"set2GConnectivityState")) { "Expected method 'set2GConnectivityState' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun get2GConnectivityState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"get2GConnectivityState")) { "Expected method 'get2GConnectivityState' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun setIMSEnabled_Exists() = runTest {
        assert(checkMethodExistence(PhoneRestrictionPolicy::class,"setIMSEnabled")) { "Expected method 'setIMSEnabled' to exist on PhoneRestrictionPolicy" }
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun isIMSEnabled_Exists() = runTest {
        assert(checkMethodExistence(PhoneRestrictionPolicy::class,"isIMSEnabled")) { "Expected method 'isIMSEnabled' to exist on PhoneRestrictionPolicy" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun setEsimEnabled_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class,"setEsimEnabled")) { "Expected method 'setEsimEnabled' to exist on SettingsManager" }
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun getEsimEnabled_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class,"getEsimEnabled")) { "Expected method 'getEsimEnabled' to exist on SettingsManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 134)
    fun setAlwaysRadioOn_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class,"setAlwaysRadioOn")) { "Expected method 'setAlwaysRadioOn' to exist on SettingsManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 134)
    fun isAlwaysRadioOn_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class,"isAlwaysRadioOn")) { "Expected method 'isAlwaysRadioOn' to exist on SettingsManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun getPLMNAllowedList_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getPLMNAllowedList")) { "Expected method 'getPLMNAllowedList' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun addPLMNAllowedList_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"addPLMNAllowedList")) { "Expected method 'addPLMNAllowedList' to exist on SystemManager" }
    }
}

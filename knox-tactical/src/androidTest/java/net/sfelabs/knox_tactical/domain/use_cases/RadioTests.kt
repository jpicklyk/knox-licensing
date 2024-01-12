package net.sfelabs.knox_tactical.domain.use_cases

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SettingsManager
import com.samsung.android.knox.custom.SystemManager
import com.samsung.android.knox.restriction.PhoneRestrictionPolicy
import com.samsung.android.knox.restriction.RestrictionPolicy
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
class RadioTests {
    @Test
    fun enableTacticalDeviceMode_Exists() {
        assert(checkMethodExistence(RestrictionPolicy::class, "enableTacticalDeviceMode"))
    }
    @Test
    fun isTacticalDeviceModeEnabled_Exists() {
        assert(checkMethodExistence(RestrictionPolicy::class, "isTacticalDeviceModeEnabled"))
    }
    @Test
    fun get5gNrModeState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"get5gNrModeState"))
    }

    @Test
    fun set5gNrModeState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"get5gNrModeState"))
    }

    @Test
    fun getLteBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getLteBandLocking"))
    }

    @Test
    fun enableLteBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"enableLteBandLocking"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun disableLteBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"disable5GBandLocking"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun get5GBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"get5GBandLocking"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun enable5GBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"enable5GBandLocking"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun disable5GBandLocking_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"disableLteBandLocking"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun set2GConnectivityState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"set2GConnectivityState"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun get2GConnectivityState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"get2GConnectivityState"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun setIMSEnabled_Exists() = runTest {
        assert(checkMethodExistence(PhoneRestrictionPolicy::class,"setIMSEnabled"))
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun isIMSEnabled_Exists() = runTest {
        assert(checkMethodExistence(PhoneRestrictionPolicy::class,"isIMSEnabled"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun setEsimEnabled_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class,"setEsimEnabled"))
    }
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun getEsimEnabled_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class,"getEsimEnabled"))
    }


}
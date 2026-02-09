package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SettingsManager
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
class WiFiTests {
    @Test
    fun getKnoxWlanZeroMtu_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getKnoxWlanZeroMtu")) { "Expected method 'getKnoxWlanZeroMtu' to exist on SystemManager" }
    }
    @Test
    fun setWlanZeroMtu_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"setWlanZeroMtu")) { "Expected method 'setWlanZeroMtu' to exist on SystemManager" }
    }
    @Test
    fun getHotspot20State_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class,"getHotspot20State")) { "Expected method 'getHotspot20State' to exist on SettingsManager" }
    }
    @Test
    fun setHotspot20State_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class,"setHotspot20State")) { "Expected method 'setHotspot20State' to exist on SettingsManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun isRandomisedMacAddressEnabled_Exists() = runTest {
        assert(checkMethodExistence(
            RestrictionPolicy::class,
            "isRandomisedMacAddressEnabled"
        )) { "Expected method 'isRandomisedMacAddressEnabled' to exist on RestrictionPolicy" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun enableRandomisedMacAddress_Exists() = runTest {
        assert(checkMethodExistence(
            RestrictionPolicy::class,
            "enableRandomisedMacAddress"
        )) { "Expected method 'enableRandomisedMacAddress' to exist on RestrictionPolicy" }
    }
}

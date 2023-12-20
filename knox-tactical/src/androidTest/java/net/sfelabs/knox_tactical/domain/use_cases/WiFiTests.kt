package net.sfelabs.knox_tactical.domain.use_cases

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SettingsManager
import com.samsung.android.knox.custom.SystemManager
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
class WiFiTests {
    @Test
    fun getKnoxWlanZeroMtu_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"getKnoxWlanZeroMtu"))
    }
    @Test
    fun setWlanZeroMtu_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"setWlanZeroMtu"))
    }
    @Test
    fun getHotspot20State_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class,"getHotspot20State"))
    }
    @Test
    fun setHotspot20State_Exists() = runTest {
        assert(checkMethodExistence(SettingsManager::class,"setHotspot20State"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun isRandomisedMacAddressEnabled_Exists() = runTest {
        assert(checkMethodExistence(
            RestrictionPolicy::class,
            "isRandomisedMacAddressEnabled"
        ))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 130)
    fun enableRandomisedMacAddress_Exists() = runTest {
        assert(checkMethodExistence(
            RestrictionPolicy::class,
            "enableRandomisedMacAddress"
        ))
    }
}
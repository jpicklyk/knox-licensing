package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.hdm.HdmManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 130)
class HdmTests {

    @Test
    fun getHdmPolicy_Exists() = runTest {
        assert(checkMethodExistence(HdmManager::class, "getHdmPolicy")) { "Expected method 'getHdmPolicy' to exist on HdmManager" }
    }

    /**
     * API stealthHwCpControl was deprecated in Android 13 MR1 (131) and replaced by a more
     * encompassing API to control all HDM policies.  See stealthHwControl()
     */
    @Test
    @TacticalSdkSuppress(maxReleaseVersion = 130)
    fun stealthCpHwControl_Exists() = runTest {
        assert(checkMethodExistence(HdmManager::class, "stealthCpHwControl")) { "Expected method 'stealthCpHwControl' to exist on HdmManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun stealthHwControl_Exists() = runTest {
        assert(checkMethodExistence(HdmManager::class, "stealthHwControl")) { "Expected method 'stealthHwControl' to exist on HdmManager" }
    }
}

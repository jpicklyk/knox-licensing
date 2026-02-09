package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 100)
class PhoneTests {
    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun getAutoCallPickupState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getAutoCallPickupState")) { "Expected method 'getAutoCallPickupState' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun setAutoCallPickupState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setAutoCallPickupState")) { "Expected method 'setAutoCallPickupState' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun getAutomaticRecordCallEnabledState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getAutomaticRecordCallEnabledState")) { "Expected method 'getAutomaticRecordCallEnabledState' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun setAutomaticRecordCallEnabledState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setAutomaticRecordCallEnabledState")) { "Expected method 'setAutomaticRecordCallEnabledState' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun addAutoCallNumber_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "addAutoCallNumber")) { "Expected method 'addAutoCallNumber' to exist on SystemManager" }
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun getAutoCallNumberList_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getAutoCallNumberList")) { "Expected method 'getAutoCallNumberList' to exist on SystemManager" }
    }
}

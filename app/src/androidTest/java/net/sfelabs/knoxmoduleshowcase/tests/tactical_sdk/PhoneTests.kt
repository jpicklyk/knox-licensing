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
        assert(checkMethodExistence(SystemManager::class, "getAutoCallPickupState"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun setAutoCallPickupState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setAutoCallPickupState"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun getAutoRecordCallEnabledState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getAutomaticRecordCallEnabledState"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun setAutoRecordCallEnabledState_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "setAutomaticRecordCallEnabledState"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun addAutoCallNumber_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "addAutoCallNumber"))
    }

    @Test
    @TacticalSdkSuppress(minReleaseVersion = 110)
    fun getAutoCallNumberList_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "getAutoCallNumberList"))
    }
}

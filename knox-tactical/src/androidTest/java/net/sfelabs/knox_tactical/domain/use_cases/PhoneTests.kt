package net.sfelabs.knox_tactical.domain.use_cases

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SystemManager
import com.samsung.android.knox.restriction.PhoneRestrictionPolicy
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
    @TacticalSdkSuppress(minReleaseVersion = 131)
    fun setSimPowerState_Exists() = runTest {
        assert(checkMethodExistence(PhoneRestrictionPolicy::class,"setSimPowerState"))
    }

}
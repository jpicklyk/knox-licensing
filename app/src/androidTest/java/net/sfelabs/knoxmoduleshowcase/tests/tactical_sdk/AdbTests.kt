package net.sfelabs.knoxmoduleshowcase.tests.tactical_sdk

import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SystemManager
import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.testing.annotations.ApiExists
import net.sfelabs.knox.core.testing.checkMethodExistence
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import org.junit.Test

@SmallTest
@ApiExists
@TacticalSdkSuppress(minReleaseVersion = 110)
class AdbTests {
    @Test
    fun executeAdbCommand_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"executeAdbCommand")) { "Expected method 'executeAdbCommand' to exist on SystemManager" }
    }

    @Test
    fun setUsbDebuggingEnabled_Exists() = runTest {
        assert(checkMethodExistence(RestrictionPolicy::class,"setUsbDebuggingEnabled")) { "Expected method 'setUsbDebuggingEnabled' to exist on RestrictionPolicy" }
    }
}

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
@TacticalSdkSuppress(minReleaseVersion = 131)
class NatTests {
    @Test
    fun executeIptablesCommand_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "executeIptablesCommand"))
    }

    @Test
    fun enableIpForwarding_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class, "enableIpForwarding"))
    }
}

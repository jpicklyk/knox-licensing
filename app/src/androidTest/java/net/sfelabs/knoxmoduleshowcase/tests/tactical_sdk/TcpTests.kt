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
class TcpTests {
    @Test
    fun isTcpDumpEnabled_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"isTcpDumpEnabled"))
    }

    @Test
    fun enableTcpDump_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"enableTcpDump"))
    }

    @Test
    fun disableTcpDump_Exists() = runTest {
        assert(checkMethodExistence(SystemManager::class,"disableTcpDump"))
    }
}

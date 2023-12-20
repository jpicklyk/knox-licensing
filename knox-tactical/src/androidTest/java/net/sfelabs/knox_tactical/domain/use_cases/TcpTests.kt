package net.sfelabs.knox_tactical.domain.use_cases

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.samsung.android.knox.custom.SystemManager
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
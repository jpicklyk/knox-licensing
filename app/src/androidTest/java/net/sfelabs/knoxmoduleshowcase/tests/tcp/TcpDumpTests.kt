package net.sfelabs.knoxmoduleshowcase.tests.tcp

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.knoxmoduleshowcase.app.checkMethodExistence
import net.sfelabs.knox_tactical.di.KnoxModule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TcpDumpTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    @Test
    fun isTcpDumpEnabled_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"isTcpDumpEnabled"))
    }

    @Test
    fun enableTcpDump_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"enableTcpDump"))
    }

    @Test
    fun disableTcpDump_Exists() = runTest {
        assert(checkMethodExistence(systemManager::class,"disableTcpDump"))
    }
}
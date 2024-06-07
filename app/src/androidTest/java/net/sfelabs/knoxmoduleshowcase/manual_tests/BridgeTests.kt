package net.sfelabs.knoxmoduleshowcase.manual_tests

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 100)
class BridgeTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private val settingsManager = KnoxModule.provideKnoxSettingsManager()
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
    }

    /**
     * Currently this will only work in Eng builds, avc denied errors need to be fixed in user bin.
     */
    @Test
    fun createBridgeSlaveDummy0() = runTest {

        val commands = listOf(
            "link set dummy0 down",
            "link add br0 type bridge",
            "link set dummy0 master br0",
            "link set dummy0 up",
            "link set dev br0 up"
        )

        commands.forEach { command ->
            println("Running command: $command")
            val apiResult = ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, command)
            delay(200)
            assert(apiResult is ApiCall.Success)
        }
    }

}
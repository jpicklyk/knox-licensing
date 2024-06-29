package net.sfelabs.knoxmoduleshowcase.manual_tests.experimental

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4::class)
class BridgeTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private val settingsManager = KnoxModule.provideKnoxSettingsManager()
    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
    }

    val allCommands = listOf(
        "link set dummy0 down",
        "link add br0 type bridge",
        "link set dummy0 master br0",
        "link set dummy0 up",
        "link set dev br0 up"
    )
    /*
    This isn't working yet as there is an issue with how back to back commands are executed.
     */
    @Test
    fun createBridgeSlaveDummy0_bulk() = runBlocking {
        allCommands.forEach { command ->
            println("Running command: $command")
            val apiResult = ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, command)
            assert(apiResult is ApiResult.Success)
        }
    }

    /**
     * Currently this will only work in if called manually one at a time.
     */
    @Test
    fun createBridgeSlaveDummy0_step1() = runTest {

        val commands = listOf(
            "link set dummy0 down"
        )

        commands.forEach { command ->
            println("Running command: $command")
            val apiResult = ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, command)
            //delay(300)
            assert(apiResult is ApiResult.Success)
        }
    }

    @Test
    fun createBridgeSlaveDummy0_step2() = runTest {

        val commands = listOf(
            "link add br0 type bridge"
        )

        commands.forEach { command ->
            println("Running command: $command")
            val apiResult = ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, command)
            //delay(800)
            assert(apiResult is ApiResult.Success)
        }
    }

    @Test
    fun createBridgeSlaveDummy0_step3() = runTest {

        val commands = listOf(
            "link set dummy0 master br0"
        )

        commands.forEach { command ->
            println("Running command: $command")
            val apiResult = ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, command)
            //delay(800)
            assert(apiResult is ApiResult.Success)
        }
    }

    @Test
    fun createBridgeSlaveDummy0_step4() = runTest {

        val commands = listOf(
            "link set dummy0 up"
        )

        commands.forEach { command ->
            println("Running command: $command")
            val apiResult = ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, command)
            //delay(800)
            assert(apiResult is ApiResult.Success)
        }
    }

    @Test
    fun createBridgeSlaveDummy0_step5() = runTest {

        val commands = listOf(
            "link set dev br0 up"
        )

        commands.forEach { command ->
            println("Running command: $command")
            val apiResult = ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, command)
            //delay(800)
            assert(apiResult is ApiResult.Success)
        }
    }

}
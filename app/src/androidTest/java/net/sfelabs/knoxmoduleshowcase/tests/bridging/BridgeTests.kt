package net.sfelabs.knoxmoduleshowcase.tests.bridging

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.runBlocking
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 132)
class BridgeTests {

    private val allCommands = listOf(
        "link set dummy0 down",
        "link add br0 type bridge",
        "link set dummy0 master br0",
        "link set dummy0 up",
        "link set dev br0 up"
    )

    @Test
    fun createBridgeSlaveDummy0_bulk() = runBlocking {
        allCommands.forEach { command ->
            println("Running command: $command")
            val apiResult = ExecuteAdbCommandUseCase().invoke(AdbHeader.IP, command)
            assert(apiResult is ApiResult.Success)
        }
    }
}
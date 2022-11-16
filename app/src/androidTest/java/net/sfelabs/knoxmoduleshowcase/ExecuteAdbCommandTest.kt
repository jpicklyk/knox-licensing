package net.sfelabs.knoxmoduleshowcase

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests require Device Administrator so must be run inside App module
 */
@RunWith(AndroidJUnit4::class)
class ExecuteAdbCommandTest {
    private val cdm: CustomDeviceManager = CustomDeviceManager.getInstance()
    private val sm: SystemManager = cdm.systemManager


    @Test
    fun testIpCommand() = runTest {
        val useCase =
            net.sfelabs.knox_tactical.domain.use_cases.tactical.adb.ExecuteAdbCommandUseCase(sm)

        val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.IP, "ip rule flush")
        assert(result is ApiCall.Success)
    }

    @Test
    fun testDhcpDbgCommand() = runTest {
        val useCase =
            net.sfelabs.knox_tactical.domain.use_cases.tactical.adb.ExecuteAdbCommandUseCase(sm)

        val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.DHCPDBG, "eth0")
        assert(result is ApiCall.Success)
    }

    @Test
    fun testPppdCommand() = runTest {
        val useCase =
            net.sfelabs.knox_tactical.domain.use_cases.tactical.adb.ExecuteAdbCommandUseCase(sm)

        val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.PPPD, "/dev/ttyACM2 file /storage/emulated/0/atak/tools/.options.new")
        assert(result is ApiCall.Success)
    }

}
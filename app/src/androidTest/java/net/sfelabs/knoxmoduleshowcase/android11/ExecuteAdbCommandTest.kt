package net.sfelabs.knoxmoduleshowcase.android11

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests require Device Administrator so must be run inside App module
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
class ExecuteAdbCommandTest {
    private val sm = KnoxModule.provideKnoxSystemManager()

    @Test
    fun testIpCommand() = runTest {
        val useCase =
            ExecuteAdbCommandUseCase(
                sm
            )

        val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.IP, "ip rule flush")
        assert(result is ApiCall.Success)
    }

    @Test
    fun testDhcpDbgCommand() = runTest {
        val useCase =
            ExecuteAdbCommandUseCase(
                sm
            )

        val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.DHCPDBG, "eth0")
        assert(result is ApiCall.Success)
    }

    @Test
    fun testPppdCommand() = runTest {
        val useCase =
            ExecuteAdbCommandUseCase(
                sm
            )

        val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.PPPD, "/dev/ttyACM2 file /storage/emulated/0/atak/tools/.options.new")
        assert(result is ApiCall.Success)
    }

}
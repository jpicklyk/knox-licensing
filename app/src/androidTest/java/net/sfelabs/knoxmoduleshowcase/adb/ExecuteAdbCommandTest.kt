package net.sfelabs.knoxmoduleshowcase.adb

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests require Device Administrator so must be run inside App module
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 110)
class ExecuteAdbCommandTest {
    private val sm = KnoxModule.provideKnoxSystemManager()

    @Test
    fun testIpCommand() = runTest {
        val useCase =
            ExecuteAdbCommandUseCase(
                sm
            )

        val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.IP, "rule flush")
        assert(result is ApiCall.Success)
    }
    @Test
    fun testIpRouting() = runTest {
        val useCase =
            ExecuteAdbCommandUseCase(
                sm
            )

        val result = useCase.invoke(net.sfelabs.knox_tactical.domain.model.AdbHeader.IP, "rule add from all uidrange 10345-10345 lookup 1021")
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
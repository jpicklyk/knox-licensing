package net.sfelabs.knoxmoduleshowcase.tests.adb

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAndGetAdbCommandUseCase
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests require Device Administrator so must be run inside App module.
 *
 * All commands used here are read-only queries that do not modify system state.
 */
@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 141)
class ExecuteAndGetAdbCommandTest {

    private val useCase = ExecuteAndGetAdbCommandUseCase()

    @Test
    fun ipLinkShow_returnsOutput() = runTest {
        val result = useCase(AdbHeader.IP, "link show")
        assertTrue("Expected success for 'ip link show'", result is ApiResult.Success)
        val output = (result as ApiResult.Success).data
        assertFalse("Expected non-empty output from 'ip link show'", output.isBlank())
    }

    @Test
    fun ipRuleShow_returnsOutput() = runTest {
        val result = useCase(AdbHeader.IP, "rule show")
        assertTrue("Expected success for 'ip rule show'", result is ApiResult.Success)
        val output = (result as ApiResult.Success).data
        assertFalse("Expected non-empty output from 'ip rule show'", output.isBlank())
    }

    @Test
    fun ipRouteShow_returnsOutput() = runTest {
        val result = useCase(AdbHeader.IP, "route show")
        assertTrue("Expected success for 'ip route show'", result is ApiResult.Success)
        val output = (result as ApiResult.Success).data
        assertFalse("Expected non-empty output from 'ip route show'", output.isBlank())
    }

    @Test
    fun ipAddrShow_returnsOutput() = runTest {
        val result = useCase(AdbHeader.IP, "addr show")
        assertTrue("Expected success for 'ip addr show'", result is ApiResult.Success)
        val output = (result as ApiResult.Success).data
        assertFalse("Expected non-empty output from 'ip addr show'", output.isBlank())
    }
}

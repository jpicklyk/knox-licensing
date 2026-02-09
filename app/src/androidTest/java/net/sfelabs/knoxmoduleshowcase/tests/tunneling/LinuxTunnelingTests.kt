package net.sfelabs.knoxmoduleshowcase.tests.tunneling

import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 132)
class LinuxTunnelingTests {

    private lateinit var uiDevice: UiDevice

    @Before
    fun setup() {
        uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    private fun verifyInterfaceExists(interfaceName: String): Boolean {
        // Check via sysfs - readable without elevated permissions
        val output = uiDevice.executeShellCommand("ls /sys/class/net/$interfaceName 2>&1")
        println("verifyInterfaceExists($interfaceName) output: $output")
        return !output.contains("No such file") && !output.contains("not found")
    }

    private fun verifyInterfaceHasAddress(interfaceName: String, expectedAddress: String): Boolean {
        // Use ifconfig which is typically available without root
        val output = uiDevice.executeShellCommand("ifconfig $interfaceName 2>&1")
        println("verifyInterfaceHasAddress($interfaceName, $expectedAddress) output: $output")
        return output.contains(expectedAddress)
    }

    /**
     * Assume you have two servers: Android device (node1) 192.168.2.200 and node2 192.168.2.201.
     * This will set up an ipip tunnel between these two hosts.
     */
    @Test
    fun configureIpipTunnel() = runTest {
        val tunnelName = "ipip0"
        val localAddr = "10.10.10.11"
        val remotePubAddr = "192.168.4.94"
//        val internalAddr = "10.1.1.0/24"
//        val remoteInternalSubnet = "/24"
        val commands = listOf(
            "link add name $tunnelName type ipip local $localAddr remote $remotePubAddr",
            "link set $tunnelName up",
        )

        //"addr add $internalAddr dev $tunnelName",
        //"route add $remoteInternalSubnet dev $tunnelName"
        var result = true
        for(command in commands) {
            val apiResult = ExecuteAdbCommandUseCase().invoke(AdbHeader.IP, command)
            result = if(apiResult is ApiResult.Success)
                result and true
            else
                false
            Thread.sleep(500)
        }
        assertTrue("All commands should succeed", result)
        assertTrue(
            "Interface $tunnelName should exist after creation",
            verifyInterfaceExists(tunnelName)
        )
    }


    @Test
    //No longer supported by Google.  Removed APIs in version 141
    @TacticalSdkSuppress(maxReleaseVersion = 140)
    fun configureTapInterface() = runTest {
        val interfaceName = "tap0"
        val ipAddress = "10.0.0.2/24"
        val commands = listOf(
            "tuntap add dev $interfaceName mode tap",
            "addr add $ipAddress dev $interfaceName",
        )
        var result = true
        for(command in commands) {
            val apiResult = ExecuteAdbCommandUseCase().invoke(AdbHeader.IP, command)
            result = if(apiResult is ApiResult.Success)
                result and true
            else
                false
            Thread.sleep(500)
        }
        assertTrue("All commands should succeed", result)
        assertTrue(
            "Interface $interfaceName should exist after creation",
            verifyInterfaceExists(interfaceName)
        )
        assertTrue(
            "Interface $interfaceName should have address $ipAddress",
            verifyInterfaceHasAddress(interfaceName, "10.0.0.2")
        )
    }

    @Test
    //No longer supported by Google.  Removed APIs in version 141
    @TacticalSdkSuppress(maxReleaseVersion = 140)
    fun configureTunInterface() = runTest {
        val interfaceName = "tun0"
        val ipAddress = "10.0.1.1/24"
        val commands = listOf(
            "tuntap add dev $interfaceName mode tun",
            "addr add $ipAddress dev $interfaceName",
            "link set $interfaceName up",
        )
        var result = true
        for(command in commands) {
            val apiResult = ExecuteAdbCommandUseCase().invoke(AdbHeader.IP, command)
            result = if(apiResult is ApiResult.Success)
                result and true
            else
                false
            Thread.sleep(500)
        }
        assertTrue("All commands should succeed", result)
        assertTrue(
            "Interface $interfaceName should exist after creation",
            verifyInterfaceExists(interfaceName)
        )
        assertTrue(
            "Interface $interfaceName should have address $ipAddress",
            verifyInterfaceHasAddress(interfaceName, "10.0.1.1")
        )
    }

}
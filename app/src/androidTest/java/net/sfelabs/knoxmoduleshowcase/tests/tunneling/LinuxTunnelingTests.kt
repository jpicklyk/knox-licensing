package net.sfelabs.knoxmoduleshowcase.tests.tunneling

import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import org.junit.Test


@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 132)
class LinuxTunnelingTests {

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
            "link name $tunnelName type ipip local $localAddr remote $remotePubAddr",
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
        assert(result)
    }


    @Test
    fun configureTapInterface() = runTest {
        //val command = "tuntap add dev tap0 mode tap"
        val commands = listOf(
            "tuntap add dev tap0 mode tap",
            "addr add 10.0.0.2/24 dev tap0",
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
        assert(result)

    }

}
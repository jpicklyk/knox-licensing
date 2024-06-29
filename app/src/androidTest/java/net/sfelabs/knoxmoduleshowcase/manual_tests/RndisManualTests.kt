package net.sfelabs.knoxmoduleshowcase.manual_tests

import android.content.Context
import android.net.ConnectivityManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.di.AndroidServiceModule
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.model.UsbConnectionType
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbConnectionTypeUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@TacticalSdkSuppress(minReleaseVersion = 100)
class RndisManualTests {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private val settingsManager = KnoxModule.provideKnoxSettingsManager()
    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
        connectivityManager = AndroidServiceModule.provideConnectivityManager(context)
    }

    /**
     * Device needs to be set up in tethering mode to connect up to the computer using RNDIS.
     * The network interface rndis0 will be created on connection
     */
    @Test
    fun setupRndisUsbConfiguration() = runTest {
        val result = SetUsbConnectionTypeUseCase(systemManager).invoke(
            UsbConnectionType.Tethering
        )
        assert(result is ApiResult.Success)
    }

    @Test
    fun setDefaultUsbConfiguration() = runTest {
        val result = SetUsbConnectionTypeUseCase(systemManager).invoke(
            UsbConnectionType.Default
        )
        assert(result is ApiResult.Success)
    }

    @Test
    fun configureRndis0Static() = runBlocking {
        val ipAddress = "10.0.2.3"
        val route = "10.0.2.0/24"
        val defaultGateway = "10.0.2.1"

        val removeRndis0 = "addr flush dev rndis0"
        val downRndis0 = "link set rndis0 down"
        val addRndis0 = "addr add ${ipAddress}/24 brd + dev rndis0"
        val upRndis0 = "link set rndis0 up"
        val routeChange = "route change $route via $defaultGateway"

        val commands = listOf(
            removeRndis0,
            downRndis0,
            addRndis0,
            upRndis0
            )
        var result = true
        for(command in commands) {
            val apiResult = ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, command)
            result = if(apiResult is ApiResult.Success)
                result and true
            else
                false
            Thread.sleep(500)
        }
        assert(result)

        //setupNetworkRulesAndRoutes(ipAddress)
    }

    @Test
    fun configureRndisDhcp() = runTest {
        ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, "addr flush dev rndis0")
        delay(350)
        val dhcpResult =ExecuteAdbCommandUseCase(systemManager).invoke(
            AdbHeader.DHCPDBG, "rndis0"
        )
        delay(350)
        /**ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, "link set rndis0 up")
         *assert(dhcpResult is ApiCall.Success)
         *delay(350)
         */

        ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, "link set rndis0 up")
        assert(dhcpResult is ApiResult.Success)
        delay(350)
        //setupNetworkRulesAndRoutes()
    }

    @Test
    fun addNetworkRoute() = runTest {
        val apiResult = ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, "route add 10.0.2.0/24 dev rndis0 src 10.0.2.3")
        Thread.sleep(150)
        assert(apiResult is ApiResult.Success)
    }


    private fun getNetworkCallback(): ConnectivityManager.NetworkCallback {
        return object: ConnectivityManager.NetworkCallback() {
        }
    }

    /**
     * Packets originating from the RNDIS configuration IP address 10.0.2.1 should be handled
     * according to tableId.
     *
     * These routes specify that all traffic should be sent out through the rndis0 interface,
     * with the exception of local traffic (255.255.255.255/32), which should be delivered directly
     * to the interface.
     */
    @Test
    fun setupNetworkRulesAndRoutes() = runBlocking {
        val broadcastAddress = "255.255.255.255"
        val ipAddress = "10.0.2.3"
        val network = "10.0.2.0/24"
        val gateway = "10.0.2.1"
        val tableId = "97"
        val device = "rndis0"
        val commands = listOf(
            "route add default via $gateway dev $device table $tableId",
            //"route add $network dev $device table $tableId",
            //"route add $network dev $device src $ipAddress",
            //"rule add from all iif lo oif $device uidrange 0-0 lookup $tableId",
            //"rule add from all iif lo oif $device lookup $tableId"

        )

        commands.forEach {command ->
            println("Running adb command: $command")
            val apiResult = ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP,command)
            assert(apiResult is ApiResult.Success)
            Thread.sleep(500)
        }
    }

}
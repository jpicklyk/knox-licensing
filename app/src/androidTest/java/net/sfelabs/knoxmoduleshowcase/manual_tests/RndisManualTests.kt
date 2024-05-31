package net.sfelabs.knoxmoduleshowcase.manual_tests

import android.content.Context
import android.net.ConnectivityManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.di.AndroidServiceModule
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.AdbHeader
import net.sfelabs.knox_tactical.domain.model.AutoConnectionState
import net.sfelabs.knox_tactical.domain.model.UsbConnectionType
import net.sfelabs.knox_tactical.domain.use_cases.adb.ExecuteAdbCommandUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetEthernetAutoConnectionUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.SetEthernetAutoConnectionUseCase
import net.sfelabs.knox_tactical.domain.use_cases.usb.SetUsbConnectionTypeUseCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
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
        assert(result is ApiCall.Success)
    }

    @Test
    fun setDefaultUsbConfiguration() = runTest {
        val result = SetUsbConnectionTypeUseCase(systemManager).invoke(
            UsbConnectionType.Default
        )
        assert(result is ApiCall.Success)
    }

    @Test
    fun disableEthernetAutoConfig() = runTest {
        val resultFlow = SetEthernetAutoConnectionUseCase(settingsManager, connectivityManager)
            .invoke(AutoConnectionState.OFF, listOf(ConnectivityManager.NetworkCallback()))

        assert(resultFlow.first() is ApiCall.Success)

        val result = GetEthernetAutoConnectionUseCase(settingsManager).invoke()
        assert(result is ApiCall.Success && result.data is AutoConnectionState.OFF)
    }

    @Test
    fun enableEthernetAutoConfig() = runTest {
        val resultFlow = SetEthernetAutoConnectionUseCase(settingsManager, connectivityManager)
            .invoke(AutoConnectionState.ON, listOf(ConnectivityManager.NetworkCallback()))

        assert(resultFlow.first() is ApiCall.Success)

        val result = GetEthernetAutoConnectionUseCase(settingsManager).invoke()
        assert(result is ApiCall.Success && result.data is AutoConnectionState.ON)
    }


    @Test
    fun configureRndis0Static() = runTest {
        val ipAddress = "192.168.236.222"
        val route = "192.168.236.0/24"
        val defaultGateway = "192.168.2.1"

        val removeRndis0 = "addr flush dev rndis0"
        val downRndis0 = "link set rndis0 down"
        val addRndis0 = "addr add ${ipAddress}/24 brd + dev rndis0"
        val upRndis0 = "link set rndis0 up"
        val routeChange = "route change $route via $defaultGateway"

        val commands = listOf(removeRndis0, addRndis0, upRndis0, routeChange)
        var result = true
        for(command in commands) {
            val apiResult = ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, command)
            result = if(apiResult is ApiCall.Success)
                result and true
            else
                false
            Thread.sleep(500)
        }
        assert(result)
        /*


                disableEthernetAutoConfig()
                val result2 = ConfigureStaticRndis0InterfaceUseCase(systemManager, connectivityManager).invoke(
                    ipAddress,
                    "255.255.255.0",
                    defaultGateway,
                    getNetworkCallback()
                )

                //enableEthernetAutoConfig()
                assert(result2 is ApiCall.Success)
                */

    }

    @Test
    fun configureRndisDhcp() = runTest {
        ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, "addr flush dev rndis0")
        Thread.sleep(250)
        val dhcpResult =ExecuteAdbCommandUseCase(systemManager).invoke(
            AdbHeader.DHCPDBG, "rndis0"
        )
        Thread.sleep(250)
        ExecuteAdbCommandUseCase(systemManager).invoke(AdbHeader.IP, "link set rndis0 up")
        assert(dhcpResult is ApiCall.Success)
        Thread.sleep(250)
    }



    private fun getNetworkCallback(): ConnectivityManager.NetworkCallback {
        return object: ConnectivityManager.NetworkCallback() {
        }
    }

}
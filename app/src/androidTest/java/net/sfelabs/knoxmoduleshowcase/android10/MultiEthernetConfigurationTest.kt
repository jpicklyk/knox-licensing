package net.sfelabs.knoxmoduleshowcase.android10

import android.content.Context
import android.net.ConnectivityManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.di.AndroidServiceModule
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.model.AutoConnectionState
import net.sfelabs.knox_tactical.domain.model.DhcpConfiguration
import net.sfelabs.knox_tactical.domain.model.StaticConfiguration
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.ConfigureEthernetInterfaceUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetEthernetAutoConnectionUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.SetEthernetAutoConnectionUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class MultiEthernetConfigurationTest {
    private val systemManager = KnoxModule.provideKnoxSystemManager()
    private val settingsManager = KnoxModule.provideKnoxSettingsManager()
    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
        connectivityManager = AndroidServiceModule.provideConnectivityManager(context)
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
    fun configureEth0DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager, systemManager)
            .invoke(DhcpConfiguration("eth0"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiCall.Success)
    }

    @Test
    fun configureEth1DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager, systemManager)
            .invoke(DhcpConfiguration("eth1"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiCall.Success)
    }

    @Test
    fun configureEth2DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager, systemManager)
            .invoke(DhcpConfiguration("eth2"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiCall.Success)
    }

    @Test
    fun configureEth3DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager, systemManager)
            .invoke(DhcpConfiguration("eth3"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiCall.Success)
    }

    @Test
    fun configureEth4DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager, systemManager)
            .invoke(DhcpConfiguration("eth4"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiCall.Success)
    }

    @Test
    fun configureEth5DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager, systemManager)
            .invoke(DhcpConfiguration("eth5"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiCall.Success)
    }

    @Test
    fun configureEth0Static() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager, systemManager)
            .invoke(StaticConfiguration(
                name = "eth1",
                ipAddress = "192.168.45.123",
                gateway = "192.168.45.1",
                netmask = "255.255.255.0",
                dnsList = listOf("8.8.8.8", "8.8.4.4")
                ), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiCall.Success)
    }

    @Test
    fun configureEth0StaticWithNullGateway() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager, systemManager)
            .invoke(StaticConfiguration(
                name = "eth1",
                ipAddress = "192.168.45.123",
                gateway = null,
                netmask = "255.255.255.0",
                dnsList = listOf("8.8.8.8", "8.8.4.4")
            ), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiCall.Success)
    }

    @Test
    fun configureEth0StaticWithNoDns() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager, systemManager)
            .invoke(StaticConfiguration(
                name = "eth1",
                ipAddress = "192.168.45.123",
                gateway = "192.168.45.1",
                netmask = "255.255.255.0",
                dnsList = emptyList()
            ), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiCall.Success)
    }

    @Test
    fun configureEth0StaticWithNoDnsAndNoGateway() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager, systemManager)
            .invoke(StaticConfiguration(
                name = "eth1",
                ipAddress = "192.168.45.123",
                gateway = null,
                netmask = "255.255.255.0",
                dnsList = emptyList()
            ), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiCall.Success)
    }


    private fun getNetworkCallback(): ConnectivityManager.NetworkCallback {
        return object: ConnectivityManager.NetworkCallback() {
        }
    }

    @After
    fun resetDefaultsToSane() = runTest {
        enableEthernetAutoConfig()
    }
}
package net.sfelabs.knoxmoduleshowcase.tests.ethernet

import android.content.Context
import android.net.ConnectivityManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_tactical.TestingVisibilityOnly
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.domain.model.AutoConnectionState
import net.sfelabs.knox_tactical.domain.model.DhcpConfiguration
import net.sfelabs.knox_tactical.domain.model.StaticConfiguration
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.ConfigureEthernetInterfaceUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.GetEthernetAutoConnectionUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.SetEthernetAutoConnectionUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.TestSetEthernetConfigurationsMultiDnsUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.TestSetEthernetConfigurationsUseCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 100)
class MultiEthernetConfigurationTest {
    private lateinit var context: Context
    private lateinit var connectivityManager: ConnectivityManager

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
        connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Test
    fun disableEthernetAutoConfig() = runTest {
        val resultFlow = SetEthernetAutoConnectionUseCase(connectivityManager)
            .invoke(AutoConnectionState.OFF, listOf(ConnectivityManager.NetworkCallback()))

        assert(resultFlow.first() is ApiResult.Success)

        val result = GetEthernetAutoConnectionUseCase().invoke()
        assert(result is ApiResult.Success && result.data is AutoConnectionState.OFF)
    }

    @Test
    fun enableEthernetAutoConfig() = runTest {
        val resultFlow = SetEthernetAutoConnectionUseCase(connectivityManager)
            .invoke(AutoConnectionState.ON, listOf(ConnectivityManager.NetworkCallback()))

        assert(resultFlow.first() is ApiResult.Success)

        val result = GetEthernetAutoConnectionUseCase().invoke()
        assert(result is ApiResult.Success && result.data is AutoConnectionState.ON)
    }

    @Test
    fun configureEth0DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager)
            .invoke(DhcpConfiguration("eth0"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth1DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager)
            .invoke(DhcpConfiguration("eth1"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth2DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager)
            .invoke(DhcpConfiguration("eth2"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth3DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager)
            .invoke(DhcpConfiguration("eth3"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth4DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager)
            .invoke(DhcpConfiguration("eth4"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth5DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager)
            .invoke(DhcpConfiguration("eth5"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth0Static() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager)
            .invoke(StaticConfiguration(
                name = "eth1",
                ipAddress = "192.168.45.123",
                gateway = "192.168.45.1",
                netmask = "255.255.255.0",
                dnsList = listOf("8.8.8.8", "8.8.4.4")
                ), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth0StaticWithNullGateway() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager)
            .invoke(StaticConfiguration(
                name = "eth1",
                ipAddress = "192.168.45.123",
                gateway = null,
                netmask = "255.255.255.0",
                dnsList = listOf("8.8.8.8", "8.8.4.4")
            ), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth0StaticWithNoDns() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager)
            .invoke(StaticConfiguration(
                name = "eth1",
                ipAddress = "192.168.45.123",
                gateway = "192.168.45.1",
                netmask = "255.255.255.0",
                dnsList = emptyList()
            ), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth0StaticWithNoDnsAndNoGateway() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager)
            .invoke(StaticConfiguration(
                name = "eth1",
                ipAddress = "192.168.45.123",
                gateway = null,
                netmask = "255.255.255.0",
                dnsList = emptyList()
            ), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth0StaticWithNullDnsAndNullGateway() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager)
            .invoke(StaticConfiguration(
                name = "eth1",
                ipAddress = "192.168.45.123",
                gateway = null,
                netmask = "255.255.255.0",
                dnsList = emptyList()
            ), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @OptIn(TestingVisibilityOnly::class)
    @Test
    fun testSetEthernetConfigurations_Nulls() = runTest {
        val result = TestSetEthernetConfigurationsUseCase().invoke(
            interfaceName = "eth0",
            ipAddress = "192.168.2.244",
            netmask = "255.255.255.0",
            dnsAddress = null,
            defaultRouter = null
        )
        assertTrue(
            "Calling setEthernetConfigurations failed: ${result.getErrorOrNull()}",
            result is ApiResult.Success && result.data
        )
    }

    @OptIn(TestingVisibilityOnly::class)
    @Test
    fun testSetEthernetConfigurations_EmptyStrings() = runTest {
        val result = TestSetEthernetConfigurationsUseCase().invoke(
            interfaceName = "eth0",
            ipAddress = "192.168.2.244",
            netmask = "255.255.255.0",
            dnsAddress = "",
            defaultRouter = ""
        )
        assertTrue(
            "Calling setEthernetConfigurations failed: ${result.getErrorOrNull()}",
            result is ApiResult.Success && result.data
        )
    }

    @OptIn(TestingVisibilityOnly::class)
    @Test
    fun testSetEthernetConfigurationsMultiDns_Nulls() = runTest {
        disableEthernetAutoConfig()
        val result = TestSetEthernetConfigurationsMultiDnsUseCase().invoke(
            interfaceName = "eth0",
            ipAddress = "192.168.2.244",
            netmask = "255.255.255.0",
            dnsList = null,
            defaultRouter = null
        )
        enableEthernetAutoConfig()
        assertTrue(
            "Calling setEthernetConfigurationsMultiDns failed: ${result.getErrorOrNull()}",
            result is ApiResult.Success && result.data
        )
    }

    @OptIn(TestingVisibilityOnly::class)
    @Test
    fun testSetEthernetConfigurationsMultiDns_EmptyStrings() = runTest {
        val result = TestSetEthernetConfigurationsMultiDnsUseCase().invoke(
            interfaceName = "eth0",
            ipAddress = "192.168.2.244",
            netmask = "255.255.255.0",
            dnsList = emptyList(),
            defaultRouter = ""
        )
        assertTrue(
            "Calling setEthernetConfigurationsMultiDns failed: ${result.getErrorOrNull()}",
            result is ApiResult.Success && result.data
        )
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
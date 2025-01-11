package net.sfelabs.knoxmoduleshowcase.tests.ethernet

import android.content.Context
import android.net.ConnectivityManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.di.AndroidServiceModule
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.knox_tactical.KnoxTacticalExtensions.testSetEthernetConfigurations
import net.sfelabs.knox_tactical.KnoxTacticalExtensions.testSetEthernetConfigurationsMultiDns
import net.sfelabs.knox_tactical.TestingVisibilityOnly
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
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
@TacticalSdkSuppress(minReleaseVersion = 100)
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

        assert(resultFlow.first() is ApiResult.Success)

        val result = GetEthernetAutoConnectionUseCase(settingsManager).invoke()
        assert(result is ApiResult.Success && result.data is AutoConnectionState.OFF)
    }

    @Test
    fun enableEthernetAutoConfig() = runTest {
        val resultFlow = SetEthernetAutoConnectionUseCase(settingsManager, connectivityManager)
            .invoke(AutoConnectionState.ON, listOf(ConnectivityManager.NetworkCallback()))

        assert(resultFlow.first() is ApiResult.Success)

        val result = GetEthernetAutoConnectionUseCase(settingsManager).invoke()
        assert(result is ApiResult.Success && result.data is AutoConnectionState.ON)
    }

    @Test
    fun configureEth0DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager,
            this@MultiEthernetConfigurationTest.systemManager
        )
            .invoke(DhcpConfiguration("eth0"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth1DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager,
            this@MultiEthernetConfigurationTest.systemManager
        )
            .invoke(DhcpConfiguration("eth1"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth2DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager,
            this@MultiEthernetConfigurationTest.systemManager
        )
            .invoke(DhcpConfiguration("eth2"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth3DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager,
            this@MultiEthernetConfigurationTest.systemManager
        )
            .invoke(DhcpConfiguration("eth3"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth4DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager,
            this@MultiEthernetConfigurationTest.systemManager
        )
            .invoke(DhcpConfiguration("eth4"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth5DHCP() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager,
            this@MultiEthernetConfigurationTest.systemManager
        )
            .invoke(DhcpConfiguration("eth5"), getNetworkCallback())
        enableEthernetAutoConfig()
        assert(resultFlow.first() is ApiResult.Success)
    }

    @Test
    fun configureEth0Static() = runTest {
        disableEthernetAutoConfig()
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager,
            this@MultiEthernetConfigurationTest.systemManager
        )
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
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager,
            this@MultiEthernetConfigurationTest.systemManager
        )
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
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager,
            this@MultiEthernetConfigurationTest.systemManager
        )
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
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager,
            this@MultiEthernetConfigurationTest.systemManager
        )
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
        val resultFlow = ConfigureEthernetInterfaceUseCase(connectivityManager,
            this@MultiEthernetConfigurationTest.systemManager
        )
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
        try {
            val result = this@MultiEthernetConfigurationTest.systemManager.testSetEthernetConfigurations(
                "eth0",
                "192.168.2.244",
                "255.255.255.0",
                null,
                null

            )
            assertTrue("Calling setEthernetConfigurations failed", result)
        }catch (npe: NullPointerException) {
            assertTrue("Null pointer exception received!",false)
        }
    }

    @OptIn(TestingVisibilityOnly::class)
    @Test
    fun testSetEthernetConfigurations_EmptyStrings() = runTest {
        try {
            val result = this@MultiEthernetConfigurationTest.systemManager.testSetEthernetConfigurations(
                "eth0",
                "192.168.2.244",
                "255.255.255.0",
                "",
                ""

            )
            assertTrue("Calling setEthernetConfigurations failed", result)
        }catch (npe: NullPointerException) {
            assertTrue("Null pointer exception received!",false)
        }
    }

    @OptIn(TestingVisibilityOnly::class)
    @Test
    fun testSetEthernetConfigurationsMultiDns_Nulls() = runTest {
        disableEthernetAutoConfig()
        try {
            val result = this@MultiEthernetConfigurationTest.systemManager.testSetEthernetConfigurationsMultiDns(
                "eth0",
                "192.168.2.244",
                "255.255.255.0",
                null,
                null

            )
            enableEthernetAutoConfig()
            assertTrue("Calling setEthernetConfigurationsMultiDns failed", result)
        }catch (npe: NullPointerException) {
            assertTrue("Null pointer exception received!",false)
        }
    }

    @OptIn(TestingVisibilityOnly::class)
    @Test
    fun testSetEthernetConfigurationsMultiDns_EmptyStrings() = runTest {
        try {
            val result = this@MultiEthernetConfigurationTest.systemManager.testSetEthernetConfigurationsMultiDns(
                "eth0",
                "192.168.2.244",
                "255.255.255.0",
                emptyList(),
                ""

            )
            assertTrue("Calling setEthernetConfigurationsMultiDns failed", result)
        }catch (npe: NullPointerException) {
            assertTrue("Null pointer exception received!",false)
        }
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
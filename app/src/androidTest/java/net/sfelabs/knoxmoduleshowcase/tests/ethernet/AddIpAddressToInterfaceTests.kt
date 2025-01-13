package net.sfelabs.knoxmoduleshowcase.tests.ethernet

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.testing.rules.EthernetNotConnected
import net.sfelabs.core.testing.rules.EthernetNotConnectedRule
import net.sfelabs.core.testing.rules.EthernetRequired
import net.sfelabs.core.testing.rules.EthernetRequiredRule
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.AddIpAddressToEthernetInterfaceUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.ListIpAddressesUseCase
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * To confirm the IP id set, you need to use: adb shell ip -4 addr
 * ifconfig will not show the IP address added for whatever reason.  The IP tool is technically
 * newer and ifconfig somewhat (or is) deprecated so this is the likely reason.
 */
@RunWith(AndroidJUnit4::class)
@SmallTest
@TacticalSdkSuppress(minReleaseVersion = 131)
class AddIpAddressToInterfaceTests {
    private val settingsManager = KnoxModule.provideKnoxSettingsManager()
    @get: Rule
    val ethernetRequiredRule = EthernetRequiredRule()
    @get: Rule
    val ethernetNotConnectedRule = EthernetNotConnectedRule()

    @Before
    fun recordCurrentConfiguration() = runTest {
        //TODO: When API becomes available to view ip address, record and replace
    }

    /**
     * Testing assigning IP address via CIDR notation.
     * 192.168.2.199/24 is equal to 129.168.2.199 with subnet mask 255.255.255.0
     */
    @Test
    @EthernetRequired
    fun addIpAddress_withPrefix24_returnsSuccess() = runTest {
        val interfaceName = "eth0"
        val ipAddress = "192.168.2.199/24"
        val result = AddIpAddressToEthernetInterfaceUseCase()
            .invoke(interfaceName, ipAddress)
        assert(result is ApiResult.Success)

        val ipAddressesResult = ListIpAddressesUseCase(settingsManager).invoke(interfaceName)
        assert(ipAddressesResult is ApiResult.Success)
        println("IP addresses: ${(ipAddressesResult as ApiResult.Success).data}")
        assert(ipAddressesResult.data.contains("/192.168.2.199/24 [/192.168.2.255]"))
    }

    /**
     * Testing assigning IP address without CIDR notation.
     * 192.168.2.199 should be taken as 129.168.2.199/24 with subnet mask 255.255.255.0
     */
    @Test
    fun addIpAddress_withoutPrefix_returnsError() = runTest {
        val interfaceName = "eth0"
        val ipAddress ="192.168.2.199"
        val result = AddIpAddressToEthernetInterfaceUseCase()
            .invoke(interfaceName, ipAddress)
        assert(result is ApiResult.Error)
    }

    /**
     * Testing assigning IP address via CIDR notation.
     * 192.168.2.199/16 is equal to 129.168.2.199 with subnet mask 255.255.0.0
     */
    @Test
    @EthernetRequired
    fun addIpAddress_withPrefix16_returnsSuccess() = runTest {
        val interfaceName = "eth0"
        val ipAddress = "192.168.2.199/16"
        val result = AddIpAddressToEthernetInterfaceUseCase()
            .invoke(interfaceName, ipAddress)
        assert(result is ApiResult.Success)

        val ipAddressesResult = ListIpAddressesUseCase(settingsManager).invoke(interfaceName)
        assert(ipAddressesResult is ApiResult.Success)
        println("IP addresses: ${(ipAddressesResult as ApiResult.Success).data}")
        assert(ipAddressesResult.data.contains("/192.168.2.199/16 [/192.168.255.255]"))
    }

    @Test
    fun addIpAddress_withInvalidInterfaceName_returnsError() = runTest {
        val interfaceNameBad = "ether0"
        val ipAddress = "192.168.2.199"
        val result = AddIpAddressToEthernetInterfaceUseCase()
            .invoke(interfaceNameBad, ipAddress)
        assert(result is ApiResult.Error)
    }

    @Test
    fun addIpAddress_withInvalidIpAddress_returnsError() = runTest {
        val interfaceNameBad = "eth0"
        val ipAddress = "192.168.2.1999"
        val result = AddIpAddressToEthernetInterfaceUseCase()
            .invoke(interfaceNameBad, ipAddress)
        assert(result is ApiResult.Error)
    }

    @Test
    @EthernetRequired
    fun addIpAddress_withPrefix29_returnsSuccess() = runTest {
        val interfaceName = "eth0"
        val ipAddress = "192.168.2.221/29"
        val result = AddIpAddressToEthernetInterfaceUseCase()
            .invoke(interfaceName, ipAddress)
        assert(result is ApiResult.Success)

        val ipAddressesResult = ListIpAddressesUseCase(settingsManager).invoke(interfaceName)
        assert(ipAddressesResult is ApiResult.Success)
        println("IP addresses: ${(ipAddressesResult as ApiResult.Success).data}")
        assert(ipAddressesResult.data.contains("/192.168.2.221/29 [/192.168.2.223]"))
    }

    @Test
    fun removeIpAddress_withInvalidInterfaceName_returnsError() = runTest {

    }

    @Test
    @EthernetNotConnected
    fun addIpAddress_noEthernetConnection_returnsError() = runTest {
        val interfaceNameBad = "eth0"
        val ipAddress = "192.168.2.22/24"
        val result = AddIpAddressToEthernetInterfaceUseCase()
            .invoke(interfaceNameBad, ipAddress)
        assertTrue(
            "Adding IP address should return an Error when there is no ethernet connection",
            result is ApiResult.Error
        )
    }


}
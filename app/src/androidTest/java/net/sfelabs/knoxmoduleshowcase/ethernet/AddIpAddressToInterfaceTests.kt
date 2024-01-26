package net.sfelabs.knoxmoduleshowcase.ethernet

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import kotlinx.coroutines.test.runTest
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.knox_tactical.annotations.TacticalSdkSuppress
import net.sfelabs.knox_tactical.di.KnoxModule
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.AddIpAddressToEthernetInterfaceUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.DeleteIpAddressFromEthernetInterfaceUseCase
import org.junit.Before
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

    @Before
    fun recordCurrentConfiguration() = runTest {
        //TODO: When API becomes available to view ip address, record and replace
    }

    /**
     * Testing assigning IP address via CIDR notation.
     * 192.168.2.199/24 is equal to 129.168.2.199 with subnet mask 255.255.255.0
     */
    @Test
    fun setSingleIpAddress_cidr_notation_24() = runTest {
        val interfaceName = "eth0"
        val ipAddress = "192.168.2.199/24"
        val result = AddIpAddressToEthernetInterfaceUseCase(settingsManager)
            .invoke(interfaceName, ipAddress)
        assert(result is ApiCall.Success)


    }

    /**
     * Testing assigning IP address without CIDR notation.
     * 192.168.2.199 should be taken as 129.168.2.199/24 with subnet mask 255.255.255.0
     */
    @Test
    fun setSingleIpAddress_implicit_cidr_notation_24() = runTest {
        val interfaceName = "eth0"
        val ipAddress ="192.168.2.199"
        val result = AddIpAddressToEthernetInterfaceUseCase(settingsManager)
            .invoke(interfaceName, ipAddress)
        assert(result is ApiCall.Error)
    }

    /**
     * Testing assigning IP address via CIDR notation.
     * 192.168.2.199/16 is equal to 129.168.2.199 with subnet mask 255.255.0.0
     */
    @Test
    fun setSingleIpAddress_cidr_notation_16() = runTest {
        val interfaceName = "eth0"
        val ipAddress = "192.168.2.199/16"
        val result = AddIpAddressToEthernetInterfaceUseCase(settingsManager)
            .invoke(interfaceName, ipAddress)
        assert(result is ApiCall.Success)
    }

    @Test
    fun addIncorrectInterfaceName() = runTest {
        val interfaceNameBad = "ether0"
        val ipAddress = "192.168.2.199"
        val result = AddIpAddressToEthernetInterfaceUseCase(settingsManager)
            .invoke(interfaceNameBad, ipAddress)
        assert(result is ApiCall.Error)
    }

    @Test
    fun addIncorrectIpAddress() = runTest {
        val interfaceNameBad = "eth0"
        val ipAddress = "192.168.2.1999"
        val result = AddIpAddressToEthernetInterfaceUseCase(settingsManager)
            .invoke(interfaceNameBad, ipAddress)
        assert(result is ApiCall.Error)
    }

    @Test
    fun removeIncorrectIpAddress() = runTest {
        val interfaceName = "eth0"
        val ipAddressBad = "192.168.2.1999"
        val result = DeleteIpAddressFromEthernetInterfaceUseCase(settingsManager)
            .invoke(interfaceName, ipAddressBad)
        assert(result is ApiCall.Error)
    }

    @Test
    fun removeIncorrectInterfaceName() = runTest {
        val interfaceNameBad = "ether0"
        val ipAddress = "192.168.2.199/24"
        val result = DeleteIpAddressFromEthernetInterfaceUseCase(settingsManager)
            .invoke(interfaceNameBad, ipAddress)
        assert(result is ApiCall.Error)
    }

    @Test
    fun clearIpAddresses() = runTest {
        val interfaceName = "eth0"
        val ipAddress = "192.168.2.199/24"
        val result = AddIpAddressToEthernetInterfaceUseCase(settingsManager)
            .invoke(interfaceName, ipAddress)
        assert(result is ApiCall.Success)
    }
}
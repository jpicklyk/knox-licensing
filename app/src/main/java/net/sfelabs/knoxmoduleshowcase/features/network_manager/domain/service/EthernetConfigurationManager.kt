package net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.service

import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.ConfigureEthernetInterfaceAltUseCase
import net.sfelabs.knox_tactical.domain.use_cases.ethernet.SetEthernetAutoConnectionAltUseCase
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceAddressConfigurationType
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceConfiguration
import net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model.InterfaceType
import java.net.NetworkInterface
import javax.inject.Inject

/**
 * Service to manage Ethernet interface configurations
 */

class EthernetConfigurationManager @Inject constructor(
    private val setEthernetAutoConfigurationAltUseCase: SetEthernetAutoConnectionAltUseCase,
    private val configureEthernetInterfaceAltUseCase: ConfigureEthernetInterfaceAltUseCase
) {

    suspend fun applyConfiguration(configurations: List<InterfaceConfiguration>) {
        setEthernetAutoConfigurationAltUseCase(false)
        configurations.filter { it.interfaceType == InterfaceType.Ethernet }.forEach {
            configureInterface(it)
        }
        setEthernetAutoConfigurationAltUseCase(true)

    }


    private fun getCurrentNetworkInterface(interfaceName: String): NetworkInterface? {
        return NetworkInterface.getNetworkInterfaces().toList().firstOrNull {
            it.name == interfaceName
        }
    }

    private suspend fun configureInterface(configuration: InterfaceConfiguration) {
        if(configuration.ipAddresses.isNotEmpty()) {
            val address = configuration.ipAddresses[0]
            val configureResult = configureEthernetInterfaceAltUseCase(
                interfaceName = configuration.interfaceName,
                useDhcp = address.type == InterfaceAddressConfigurationType.DHCP,
                ipAddress = address.ipAddress,
                netmask = address.netmask,
                dnsAddressList = address.dnsList,
                defaultRouter = address.gateway
            )
            if(configureResult is ApiResult.Error) {
                println("Configuration failed: ${configureResult.uiText}")
            }

        }
    }
}
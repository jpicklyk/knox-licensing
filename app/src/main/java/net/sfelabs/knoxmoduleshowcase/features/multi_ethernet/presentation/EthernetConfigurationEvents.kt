package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.presentation

import net.sfelabs.knox_tactical.domain.model.EthernetInterfaceType

sealed class EthernetConfigurationEvents {
    object EnableEthernetAutoConnection: EthernetConfigurationEvents()
    object DisableEthernetAutoConnection: EthernetConfigurationEvents()
    data class EnteredInterfaceName(val value: String): EthernetConfigurationEvents()
    data class SelectedInterfaceType(val value: EthernetInterfaceType): EthernetConfigurationEvents()
    data class EnteredIpAddress(val value: String): EthernetConfigurationEvents()
    data class EnteredNetmask(val value: String): EthernetConfigurationEvents()
    data class EnteredDefaultGateway(val value: String): EthernetConfigurationEvents()
    data class EnteredDnsList(val value: String): EthernetConfigurationEvents()
    object SaveConfiguration: EthernetConfigurationEvents()
}

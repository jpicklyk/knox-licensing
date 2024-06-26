package net.sfelabs.knoxmoduleshowcase.features.network_manager.domain.model

sealed class InterfaceAddressConfigurationType(val value: String) {
    data object Static : InterfaceAddressConfigurationType(value = "Static")
    data object DHCP : InterfaceAddressConfigurationType(value = "DHCP")

    companion object {
        val allTypes: List<InterfaceAddressConfigurationType> get() = listOf(DHCP, Static)
    }
}
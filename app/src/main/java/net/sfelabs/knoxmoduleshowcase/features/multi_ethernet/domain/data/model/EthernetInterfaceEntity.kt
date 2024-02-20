package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model

data class EthernetInterfaceEntity(
    val id: Int,
    val name: String,
    val type: String,
    val netmask: String,
    val ip: String,
    val gateway: String? = null,
    val mac: String? = null,
    val dnsList: List<String> = emptyList()
)
package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import net.sfelabs.knox_tactical.domain.model.DhcpConfiguration
import net.sfelabs.knox_tactical.domain.model.EthernetInterface
import net.sfelabs.knox_tactical.domain.model.EthernetInterfaceType
import net.sfelabs.knox_tactical.domain.model.StaticConfiguration
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.EthernetInterfaceEntity
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.store.EthernetInterfaceDataStore
import javax.inject.Inject

class EthernetInterfaceRepository @Inject constructor(
    private val dataStore: EthernetInterfaceDataStore
) {

    fun fetchAllInterfaces(): Flow<List<EthernetInterface>> = dataStore.getAll()
        .transform { entities ->
            val interfaces: MutableList<EthernetInterface> = mutableListOf()
            entities.forEach {
                when (EthernetInterfaceType(it.type)) {
                    is EthernetInterfaceType.DHCP -> interfaces.add(DhcpConfiguration(it.name))
                    is EthernetInterfaceType.STATIC -> interfaces.add(
                        StaticConfiguration(
                            name = it.name,
                            ipAddress = it.ip,
                            gateway = it.gateway,
                            netmask = it.netmask,
                            dnsList = it.dnsList
                        )
                    )
                }
            }
            emit(interfaces)
        }


    fun fetchInterface(name: String): EthernetInterface? {
        val entity = dataStore.getByName(name) ?: return null
        return when (EthernetInterfaceType(entity.type)) {
            is EthernetInterfaceType.DHCP -> DhcpConfiguration(entity.name)
            is EthernetInterfaceType.STATIC -> StaticConfiguration(
                name = entity.name,
                ipAddress = entity.ip,
                gateway = entity.gateway,
                netmask = entity.netmask,
                dnsList = entity.dnsList
            )
        }
    }

    fun saveInterface(eth: EthernetInterface) {
        dataStore.save(eth.name, eth.toEntity())
    }

    private fun EthernetInterface.toEntity(): EthernetInterfaceEntity {
        return when (this) {
            is DhcpConfiguration -> {
                EthernetInterfaceEntity(
                    id = 0,
                    name = this.name,
                    type = this.type.interfaceType,
                    netmask = "255.255.255.0",
                    ip = "192.168.2.230",
                    gateway = null,
                    mac = null,
                    dnsList = emptyList()
                )
            }

            is StaticConfiguration -> {
                EthernetInterfaceEntity(
                    id = 0,
                    name = this.name,
                    type = this.type.interfaceType,
                    netmask = this.netmask,
                    ip = this.ipAddress,
                    gateway = this.gateway,
                    mac = null,
                    dnsList = this.dnsList
                )
            }
        }
    }
}
package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
import net.sfelabs.knox_tactical.domain.model.DhcpEthernetInterface
import net.sfelabs.knox_tactical.domain.model.EthernetInterface
import net.sfelabs.knox_tactical.domain.model.EthernetInterfaceType
import net.sfelabs.knox_tactical.domain.model.StaticEthernetInterface
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
                    is EthernetInterfaceType.DHCP -> interfaces.add(DhcpEthernetInterface(it.name))
                    is EthernetInterfaceType.STATIC -> interfaces.add(
                        StaticEthernetInterface(
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
            is EthernetInterfaceType.DHCP -> DhcpEthernetInterface(entity.name)
            is EthernetInterfaceType.STATIC -> StaticEthernetInterface(
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
            is DhcpEthernetInterface -> {
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

            is StaticEthernetInterface -> {
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
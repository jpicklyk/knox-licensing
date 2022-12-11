package net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.store

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.sfelabs.knoxmoduleshowcase.features.multi_ethernet.domain.data.model.EthernetInterfaceEntity
import javax.inject.Singleton

@Singleton
sealed class EthernetInterfaceDataStore {
    private val interfaces: HashMap<String, EthernetInterfaceEntity> = HashMap()

    fun getAll(): Flow<List<EthernetInterfaceEntity>> = flow {
        emit(interfaces.values.toMutableList())
    }

    fun getByName(name: String): EthernetInterfaceEntity? {
        return interfaces.getOrDefault(name, null)
    }

    fun save(name: String, entity: EthernetInterfaceEntity) {
        interfaces[name] = entity
    }

}

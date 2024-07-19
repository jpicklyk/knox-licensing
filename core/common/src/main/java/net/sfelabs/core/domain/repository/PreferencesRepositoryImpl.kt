package net.sfelabs.core.domain.repository

import kotlinx.coroutines.flow.Flow
import net.sfelabs.core.data.datasource.DataStoreSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dataSource: DataStoreSource
) : PreferencesRepository {
    override suspend fun <T> setValue(key: String, value: T) {
        dataSource.setValue(key, value)
    }

    override fun <T> getValue(key: String, defaultValue: T): Flow<T> {
        return dataSource.getValue(key, defaultValue)
    }
}
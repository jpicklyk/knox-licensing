package net.sfelabs.core.data.datasource

import kotlinx.coroutines.flow.Flow

interface DataStoreSource {
    suspend fun <T> setValue(key: String, value: T)
    fun <T> getValue(key: String, defaultValue: T): Flow<T>
}
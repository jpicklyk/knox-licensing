package net.sfelabs.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    suspend fun <T> setValue(key: String, value: T)
    fun <T> getValue(key: String, defaultValue: T): Flow<T>
}
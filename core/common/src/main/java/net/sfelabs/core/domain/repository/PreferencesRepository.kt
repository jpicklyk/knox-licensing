package net.sfelabs.core.domain.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import net.sfelabs.core.data.datasource.DataStoreSource

interface PreferencesRepository {
    suspend fun <T> setValue(key: String, value: T)
    fun <T> getValue(key: String, defaultValue: T): Flow<T>

    companion object Factory {
        private var instance: PreferencesRepository? = null

        @Synchronized
        fun getInstance(context: Context? = null): PreferencesRepository {
            if (instance == null && context == null) {
                throw IllegalStateException("Context must be provided for first initialization")
            }

            return instance ?: context?.let { ctx ->
                object : PreferencesRepository {
                    private val dataStore = DataStoreSource.getInstance(ctx)

                    override suspend fun <T> setValue(key: String, value: T) {
                        dataStore.setValue(key, value)
                    }

                    override fun <T> getValue(key: String, defaultValue: T): Flow<T> {
                        return dataStore.getValue(key, defaultValue)
                    }
                }.also {
                    instance = it
                }
            } ?: instance!!
        }

        // For testing purposes
        internal fun reset() {
            instance = null
        }
    }
}
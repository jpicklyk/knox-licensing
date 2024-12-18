package net.sfelabs.core.data.datasource

import android.content.Context
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import okio.Path.Companion.toPath

interface DataStoreSource {
    suspend fun <T> setValue(key: String, value: T)
    fun <T> getValue(key: String, defaultValue: T): Flow<T>

    companion object Factory {
        private const val DATASTORE_FILE_NAME = "knox_showcase_settings.pb"
        private var instance: DataStoreSource? = null

        @Synchronized
        fun getInstance(context: Context? = null): DataStoreSource {
            if (instance == null && context == null) {
                throw IllegalStateException("Context must be provided for first initialization")
            }

            return instance ?: context?.let { ctx ->
                val dataStore = PreferenceDataStoreFactory.createWithPath(
                    produceFile = {
                        ctx.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath.toPath()
                    }
                )

                object : DataStoreSource {
                    override suspend fun <T> setValue(key: String, value: T) {
                        dataStore.edit { preferences ->
                            when (value) {
                                is String -> preferences[stringPreferencesKey(key)] = value
                                is Int -> preferences[intPreferencesKey(key)] = value
                                is Boolean -> preferences[booleanPreferencesKey(key)] = value
                                is Float -> preferences[floatPreferencesKey(key)] = value
                                is Long -> preferences[longPreferencesKey(key)] = value
                                is Set<*> -> {
                                    if (value.all { it is String }) {
                                        @Suppress("UNCHECKED_CAST")
                                        preferences[stringSetPreferencesKey(key)] = value as Set<String>
                                    } else {
                                        throw IllegalArgumentException("Set must contain only String values")
                                    }
                                }
                                else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
                            }
                        }
                    }

                    override fun <T> getValue(key: String, defaultValue: T): Flow<T> {
                        @Suppress("UNCHECKED_CAST")
                        return when (defaultValue) {
                            is String -> getTypedValue(key, defaultValue, ::stringPreferencesKey)
                            is Int -> getTypedValue(key, defaultValue, ::intPreferencesKey)
                            is Boolean -> getTypedValue(key, defaultValue, ::booleanPreferencesKey)
                            is Float -> getTypedValue(key, defaultValue, ::floatPreferencesKey)
                            is Long -> getTypedValue(key, defaultValue, ::longPreferencesKey)
                            is Set<*> -> {
                                if (defaultValue.all { it is String }) {
                                    getTypedValue(key, defaultValue as Set<String>, ::stringSetPreferencesKey)
                                } else {
                                    throw IllegalArgumentException("Set must contain only String values")
                                }
                            }
                            else -> throw IllegalArgumentException("This type cannot be retrieved from Preferences")
                        } as Flow<T>
                    }

                    private fun <T> getTypedValue(
                        key: String,
                        defaultValue: T,
                        keyFactory: (String) -> Preferences.Key<T>
                    ): Flow<T> = dataStore.data.map { preferences ->
                        preferences[keyFactory(key)] ?: defaultValue
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
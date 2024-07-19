package net.sfelabs.core.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreSourceImpl @Inject constructor(@ApplicationContext context: Context) : DataStoreSource {
    private val dataStore: DataStore<Preferences> = context.dataStore

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "knox_showcase_settings")
    }

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
            is String -> getStringValue(key, defaultValue) as Flow<T>
            is Int -> getIntValue(key, defaultValue) as Flow<T>
            is Boolean -> getBooleanValue(key, defaultValue) as Flow<T>
            is Float -> getFloatValue(key, defaultValue) as Flow<T>
            is Long -> getLongValue(key, defaultValue) as Flow<T>
            is Set<*> -> getStringSetValue(key, defaultValue as Set<String>) as Flow<T>
            else -> throw IllegalArgumentException("This type cannot be retrieved from Preferences")
        }
    }

    private fun getStringValue(key: String, defaultValue: String): Flow<String> =
        dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)] ?: defaultValue
        }

    private fun getIntValue(key: String, defaultValue: Int): Flow<Int> =
        dataStore.data.map { preferences ->
            preferences[intPreferencesKey(key)] ?: defaultValue
        }

    private fun getBooleanValue(key: String, defaultValue: Boolean): Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(key)] ?: defaultValue
        }

    private fun getFloatValue(key: String, defaultValue: Float): Flow<Float> =
        dataStore.data.map { preferences ->
            preferences[floatPreferencesKey(key)] ?: defaultValue
        }

    private fun getLongValue(key: String, defaultValue: Long): Flow<Long> =
        dataStore.data.map { preferences ->
            preferences[longPreferencesKey(key)] ?: defaultValue
        }

    private fun getStringSetValue(key: String, defaultValue: Set<String>): Flow<Set<String>> =
        dataStore.data.map { preferences ->
            preferences[stringSetPreferencesKey(key)] ?: defaultValue
        }
}
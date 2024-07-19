package net.sfelabs.core.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath

internal const val DATASTORE_FILE_NAME = "knox_showcase_settings.pb"

fun createDataStore(context: Context): DataStore<Preferences> {
    return createDataStore { context.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath }
}

fun createDataStore(produceLocation: () -> String) : DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { produceLocation().toPath() }
    )
}
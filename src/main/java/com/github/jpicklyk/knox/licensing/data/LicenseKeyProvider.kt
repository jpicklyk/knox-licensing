package com.github.jpicklyk.knox.licensing.data

import android.util.Log
import com.github.jpicklyk.knox.licensing.BuildConfig
import com.github.jpicklyk.knox.licensing.domain.LicenseConfiguration
internal class LicenseKeyProvider {
    private val tag = "LicenseKeyProvider"

    fun fromBuildConfig(): LicenseConfiguration {
        return try {
            val defaultKey = BuildConfig.KNOX_LICENSE_KEY
            val keysArray = BuildConfig.KNOX_LICENSE_KEYS

            val namedKeys = keysArray?.let { parseKeysArray(it) } ?: emptyMap()

            LicenseConfiguration(defaultKey, namedKeys)
        } catch (e: Exception) {
            Log.e(tag, "Error parsing BuildConfig for license keys", e)
            LicenseConfiguration("KNOX_LICENSE_KEY_NOT_FOUND")
        }
    }

    private fun parseKeysArray(keysArray: Array<String>): Map<String, String> {
        return keysArray.mapNotNull { entry ->
            try {
                val parts = entry.split(":", limit = 2)
                if (parts.size == 2) {
                    parts[0] to parts[1]
                } else {
                    Log.w(tag, "Invalid key format: $entry")
                    null
                }
            } catch (e: Exception) {
                Log.w(tag, "Error parsing key entry: $entry", e)
                null
            }
        }.toMap().filterKeys { it != "default" } // Exclude default key from named keys
    }
}
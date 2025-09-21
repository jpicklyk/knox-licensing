package com.github.jpicklyk.knox.licensing.data

import android.util.Log
import com.github.jpicklyk.knox.licensing.BuildConfig
import com.github.jpicklyk.knox.licensing.domain.LicenseConfiguration
import net.sfelabs.knox_tactical.domain.model.TacticalEditionReleases
internal class LicenseKeyProvider {
    private val tag = "LicenseKeyProvider"

    fun fromBuildConfig(): LicenseConfiguration {
        return try {
            val defaultKey = getAppropriateDefaultKey()
            val keysArray = BuildConfig.KNOX_LICENSE_KEYS

            val namedKeys = keysArray?.let { parseKeysArray(it) } ?: emptyMap()

            LicenseConfiguration(defaultKey, namedKeys)
        } catch (e: Exception) {
            Log.e(tag, "Error parsing BuildConfig for license keys", e)
            LicenseConfiguration("KNOX_LICENSE_KEY_NOT_FOUND")
        }
    }

    private fun getAppropriateDefaultKey(): String {
        return try {
            val isTe3 = TacticalEditionReleases.isCurrentDeviceTe3()
            Log.d(tag, "Device TE3 detection result: $isTe3")

            if (isTe3) {
                Log.d(tag, "TE3 device detected, checking for tactical license...")
                val tacticalKey = BuildConfig.KNOX_TACTICAL_LICENSE_KEY
                Log.d(tag, "Tactical license key from BuildConfig: ${tacticalKey.take(10)}...")

                if (tacticalKey != "KNOX_TACTICAL_LICENSE_KEY_NOT_FOUND") {
                    Log.d(tag, "Using tactical license for TE3 device")
                    tacticalKey
                } else {
                    Log.w(tag, "No tactical license configured, falling back to standard license")
                    BuildConfig.KNOX_LICENSE_KEY
                }
            } else {
                Log.d(tag, "Non-TE3 device, using standard license")
                BuildConfig.KNOX_LICENSE_KEY
            }
        } catch (e: Exception) {
            Log.w(tag, "Error determining appropriate license key, using default", e)
            BuildConfig.KNOX_LICENSE_KEY
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
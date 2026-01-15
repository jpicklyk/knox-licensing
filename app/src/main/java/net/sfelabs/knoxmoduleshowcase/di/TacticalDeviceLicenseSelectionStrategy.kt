package net.sfelabs.knoxmoduleshowcase.di

import android.util.Log
import com.github.jpicklyk.knox.licensing.domain.LicenseSelectionStrategy
import net.sfelabs.knox_tactical.domain.model.TacticalEditionReleases

/**
 * License selection strategy that automatically selects the appropriate license key
 * based on device type detection.
 *
 * - On TE3 (Tactical Edition 3) devices: selects the "tactical" license key
 * - On non-tactical devices: uses the default license key
 */
class TacticalDeviceLicenseSelectionStrategy : LicenseSelectionStrategy {

    override fun selectLicenseKey(availableKeys: Map<String, String>, defaultKey: String): String {
        val isTacticalDevice = TacticalEditionReleases.isCurrentDeviceTe3()
        Log.d(TAG, "Device type detection: isTacticalDevice=$isTacticalDevice")
        Log.d(TAG, "Available keys: ${availableKeys.keys}")

        return if (isTacticalDevice && availableKeys.containsKey(TACTICAL_KEY_NAME)) {
            val tacticalKey = availableKeys[TACTICAL_KEY_NAME]!!
            Log.d(TAG, "Selected tactical license key for TE3 device")
            tacticalKey
        } else {
            Log.d(TAG, "Selected default license key")
            defaultKey
        }
    }

    companion object {
        private const val TAG = "TacticalDeviceLicenseSelectionStrategy"
        private const val TACTICAL_KEY_NAME = "tactical"
    }
}

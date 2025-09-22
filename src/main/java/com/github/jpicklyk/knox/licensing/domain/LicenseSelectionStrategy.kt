package com.github.jpicklyk.knox.licensing.domain

/**
 * Strategy interface for selecting the appropriate license key based on device characteristics
 * or application-specific logic.
 *
 * This allows consuming applications to inject their own license selection logic without
 * creating dependencies on device-specific modules.
 */
interface LicenseSelectionStrategy {
    /**
     * Selects the appropriate license key from available options.
     *
     * @param availableKeys Map of named license keys (e.g., "tactical" -> "key123")
     * @param defaultKey The default license key to use if no specific selection is made
     * @return The selected license key to use for Knox activation
     */
    fun selectLicenseKey(availableKeys: Map<String, String>, defaultKey: String): String
}
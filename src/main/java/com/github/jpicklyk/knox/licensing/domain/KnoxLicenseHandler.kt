package com.github.jpicklyk.knox.licensing.domain

import kotlinx.coroutines.flow.Flow

/**
 * Main interface for Knox license operations
 */
interface KnoxLicenseHandler {
    /**
     * Activate a Knox license by name
     * @param licenseName Name of the license to activate (default: "default")
     * @return LicenseResult indicating success or failure
     */
    suspend fun activate(licenseName: String = "default"): LicenseResult

    /**
     * Deactivate a Knox license by name
     * @param licenseName Name of the license to deactivate (default: "default")
     * @return LicenseResult indicating success or failure
     */
    suspend fun deactivate(licenseName: String = "default"): LicenseResult

    /**
     * Get current license information
     * @return LicenseInfo with current license state
     */
    suspend fun getLicenseInfo(): LicenseInfo

    /**
     * Observe license state changes
     * @return Flow of LicenseState updates
     */
    fun observeLicenseState(): Flow<LicenseState>

    /**
     * Get all available license keys
     * @return Map of license names to license keys
     */
    fun getAvailableLicenses(): Map<String, String>

    /**
     * Check if a license name is available
     * @param licenseName Name to check
     * @return true if the license name exists
     */
    fun hasLicense(licenseName: String): Boolean
}
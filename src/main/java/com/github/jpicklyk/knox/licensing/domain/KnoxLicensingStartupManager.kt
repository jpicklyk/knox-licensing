package com.github.jpicklyk.knox.licensing.domain

import android.content.Context

/**
 * Facade object for Knox license initialization that maintains backward compatibility.
 *
 * This object delegates to a [KnoxLicenseInitializer] instance internally.
 *
 * ## For new code
 * Prefer injecting [KnoxLicenseInitializer] directly via Hilt for better testability.
 *
 * ## For existing code
 * Continue using [KnoxStartupManager] - it will use the Hilt-provided instance if available.
 */
object KnoxStartupManager {
    @Volatile
    private var initializer: KnoxLicenseInitializer? = null

    private fun getInitializer(): KnoxLicenseInitializer {
        return initializer ?: KnoxLicenseInitializer().also { initializer = it }
    }

    /**
     * Sets the initializer instance. Used by DI frameworks (like Hilt) to provide
     * their managed instance.
     *
     * @param instance The [KnoxLicenseInitializer] instance to use
     */
    @Synchronized
    fun setInstance(instance: KnoxLicenseInitializer) {
        initializer = instance
    }

    /**
     * Gets the initializer instance for direct access.
     * Prefer using [KnoxLicenseInitializer] injection when possible.
     */
    @Synchronized
    fun getInstance(): KnoxLicenseInitializer = getInitializer()

    suspend fun initializeKnoxLicensing(
        context: Context,
        licenseSelectionStrategy: LicenseSelectionStrategy? = null
    ): LicenseStartupResult {
        return getInitializer().initialize(context, licenseSelectionStrategy)
    }

    suspend fun initializeKnoxLicensing(
        context: Context,
        licenseSelectionStrategy: LicenseSelectionStrategy? = null,
        defaultKey: String? = null,
        namedKeysArray: Array<String>? = null
    ): LicenseStartupResult {
        return getInitializer().initialize(context, licenseSelectionStrategy, defaultKey, namedKeysArray)
    }

    fun isKnoxLicenseReady(): Boolean = getInitializer().isReady

    fun getLicenseStatus(): LicenseStartupResult = getInitializer().getStatus()

    /**
     * Resets the initialization state. For testing purposes only.
     */
    fun reset() {
        initializer?.reset()
        initializer = null
    }
}

sealed class LicenseStartupResult {
    object NotChecked : LicenseStartupResult()
    object AlreadyActivated : LicenseStartupResult()
    object ActivatedNow : LicenseStartupResult()
    data class ActivationFailed(val reason: String) : LicenseStartupResult()
    data class InitializationError(val reason: String) : LicenseStartupResult()

    override fun toString(): String {
        return when (this) {
            NotChecked -> "NotChecked"
            AlreadyActivated -> "AlreadyActivated"
            ActivatedNow -> "ActivatedNow"
            is ActivationFailed -> "ActivationFailed: $reason"
            is InitializationError -> "InitializationError: $reason"
        }
    }
}
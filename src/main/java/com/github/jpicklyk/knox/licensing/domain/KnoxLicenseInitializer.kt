package com.github.jpicklyk.knox.licensing.domain

import android.content.Context
import android.util.Log
import com.github.jpicklyk.knox.licensing.KnoxLicenseFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

/**
 * Manages Knox license initialization and status tracking.
 *
 * This class can be:
 * - Instantiated directly for DI-agnostic usage
 * - Provided by Hilt via knox-hilt module
 * - Accessed via [KnoxStartupManager] for legacy compatibility
 *
 * ## Usage with Hilt
 * ```kotlin
 * @Inject lateinit var licenseInitializer: KnoxLicenseInitializer
 *
 * // In your initialization code:
 * val result = licenseInitializer.initialize(context)
 * ```
 *
 * ## Usage without Hilt
 * ```kotlin
 * val result = KnoxStartupManager.initializeKnoxLicensing(context)
 * ```
 */
class KnoxLicenseInitializer {
    private val _licenseStatus = MutableStateFlow<LicenseStartupResult>(LicenseStartupResult.NotChecked)

    /**
     * Observable license status. Use this for reactive UI updates.
     */
    val licenseStatus: StateFlow<LicenseStartupResult> = _licenseStatus.asStateFlow()

    /**
     * Whether Knox licensing has been successfully activated.
     */
    val isReady: Boolean
        get() = _licenseStatus.value is LicenseStartupResult.AlreadyActivated ||
                _licenseStatus.value is LicenseStartupResult.ActivatedNow

    /**
     * Whether initialization has been attempted.
     */
    val isInitialized: Boolean
        get() = _licenseStatus.value != LicenseStartupResult.NotChecked

    /**
     * Gets the current license status.
     */
    fun getStatus(): LicenseStartupResult = _licenseStatus.value

    /**
     * Initializes Knox licensing with license keys from the app's BuildConfig.
     *
     * @param context Application context
     * @param defaultKey Default license key from app's BuildConfig.KNOX_LICENSE_KEY
     * @param namedKeysArray Array of named license keys from app's BuildConfig.KNOX_LICENSE_KEYS
     * @param licenseSelectionStrategy Optional strategy for selecting which license to use
     * @return The result of the initialization attempt
     */
    suspend fun initialize(
        context: Context,
        defaultKey: String,
        namedKeysArray: Array<String>? = null,
        licenseSelectionStrategy: LicenseSelectionStrategy? = null
    ): LicenseStartupResult {
        // Skip if already initialized successfully
        if (isInitialized && isReady) {
            Log.d(TAG, "Knox licensing already initialized: ${_licenseStatus.value}")
            return _licenseStatus.value
        }

        val result = withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Initializing Knox licensing...")
                val knoxLicenseHandler = KnoxLicenseFactory.create(
                    context,
                    licenseSelectionStrategy,
                    defaultKey,
                    namedKeysArray
                )

                // Check current status first
                val licenseInfo = knoxLicenseHandler.getLicenseInfo()
                if (licenseInfo.isActivated) {
                    Log.d(TAG, "Knox license already activated")
                    LicenseStartupResult.AlreadyActivated
                } else {
                    // Attempt activation
                    Log.d(TAG, "Attempting Knox license activation...")
                    when (val activationResult = knoxLicenseHandler.activate()) {
                        is LicenseResult.Success -> {
                            Log.d(TAG, "Knox license activated successfully: ${activationResult.message}")
                            LicenseStartupResult.ActivatedNow
                        }
                        is LicenseResult.Error -> {
                            Log.e(TAG, "Knox license activation failed: ${activationResult.message}")
                            LicenseStartupResult.ActivationFailed(activationResult.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Knox license initialization error", e)
                LicenseStartupResult.InitializationError(e.message ?: "Unknown error")
            }
        }

        _licenseStatus.value = result
        Log.d(TAG, "Knox licensing initialization complete: $result")
        return result
    }

    /**
     * Resets the initialization state. For testing purposes only.
     */
    internal fun reset() {
        _licenseStatus.value = LicenseStartupResult.NotChecked
    }

    companion object {
        private const val TAG = "KnoxLicenseInit"
    }
}

package com.github.jpicklyk.knox.licensing.domain

import android.content.Context
import android.util.Log
import com.github.jpicklyk.knox.licensing.KnoxLicenseFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object KnoxStartupManager {
    private const val TAG = "KnoxLicensingStartupManager"
    private var isInitialized = false
    private var licenseStatus: LicenseStartupResult = LicenseStartupResult.NotChecked

    suspend fun initializeKnoxLicensing(context: Context): LicenseStartupResult {
        if (isInitialized) {
            Log.d(TAG, "Knox licensing already initialized: $licenseStatus")
            return licenseStatus
        }

        licenseStatus = withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Initializing Knox licensing...")
                val knoxLicenseHandler = KnoxLicenseFactory.createFromBuildConfig(context)

                // Check current status first
                val licenseInfo = knoxLicenseHandler.getLicenseInfo()
                if (licenseInfo.isActivated) {
                    Log.d(TAG, "Knox license already activated")
                    LicenseStartupResult.AlreadyActivated
                } else {
                    // Attempt activation
                    Log.d(TAG, "Attempting Knox license activation...")
                    when (val result = knoxLicenseHandler.activate()) {
                        is LicenseResult.Success -> {
                            Log.d(TAG, "Knox license activated successfully: ${result.message}")
                            LicenseStartupResult.ActivatedNow
                        }
                        is LicenseResult.Error -> {
                            Log.e(TAG, "Knox license activation failed: ${result.message}")
                            LicenseStartupResult.ActivationFailed(result.message)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Knox license initialization error", e)
                LicenseStartupResult.InitializationError(e.message ?: "Unknown error")
            }
        }

        isInitialized = true
        Log.d(TAG, "Knox licensing initialization complete: $licenseStatus")
        return licenseStatus
    }

    fun isKnoxLicenseReady(): Boolean {
        return licenseStatus is LicenseStartupResult.AlreadyActivated ||
               licenseStatus is LicenseStartupResult.ActivatedNow
    }

    fun getLicenseStatus(): LicenseStartupResult = licenseStatus

    fun reset() {
        isInitialized = false
        licenseStatus = LicenseStartupResult.NotChecked
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
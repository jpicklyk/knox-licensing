package com.github.jpicklyk.knox.licensing.data

import android.app.admin.DevicePolicyManager
import android.content.Context
import android.util.Log
import com.github.jpicklyk.knox.licensing.domain.LicenseConfiguration
import com.github.jpicklyk.knox.licensing.domain.LicenseInfo
import com.github.jpicklyk.knox.licensing.domain.LicenseResult
import com.github.jpicklyk.knox.licensing.domain.LicenseState
import com.samsung.android.knox.license.ActivationInfo
import com.samsung.android.knox.license.KnoxEnterpriseLicenseManager
import com.samsung.android.knox.license.LicenseResultCallback
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

internal class KnoxLicenseRepository(
    private val context: Context,
    private val licenseConfiguration: LicenseConfiguration,
    private val knoxErrorMapper: KnoxErrorMapper
) {
    private val tag = "KnoxLicenseRepository"

    private val _licenseState = MutableStateFlow<LicenseState>(LicenseState.Loading)
    val licenseState: StateFlow<LicenseState> = _licenseState.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            refreshLicenseState()
        }
    }

    suspend fun activateLicense(licenseName: String): LicenseResult {
        return try {
            val licenseKey = licenseConfiguration.getKey(licenseName)
            Log.d(tag, "Activating license: $licenseName")
            Log.d(tag, "License key for '$licenseName': ${licenseKey.take(10)}...")
            Log.d(tag, "Available license keys: ${licenseConfiguration.getAllKeyNames()}")
            _licenseState.value = LicenseState.Loading

            val result = activateKnoxLicense(licenseKey)

            _licenseState.value = when (result) {
                is LicenseResult.Success -> LicenseState.Activated(result.message)
                is LicenseResult.Error -> LicenseState.Error(result.message)
            }

            refreshLicenseState()
            result
        } catch (e: IllegalArgumentException) {
            Log.e(tag, "License key not found: $licenseName", e)
            Log.e(tag, "Available keys: ${licenseConfiguration.getAllKeyNames()}")
            val errorResult = LicenseResult.Error(e.message ?: "License key not found")
            _licenseState.value = LicenseState.Error(errorResult.message)
            errorResult
        }
    }

    suspend fun deactivateLicense(licenseName: String): LicenseResult {
        return try {
            val licenseKey = licenseConfiguration.getKey(licenseName)
            Log.d(tag, "Deactivating license: $licenseName")
            _licenseState.value = LicenseState.Loading

            val result = deactivateKnoxLicense(licenseKey)

            _licenseState.value = when (result) {
                is LicenseResult.Success -> LicenseState.Deactivated(result.message)
                is LicenseResult.Error -> LicenseState.Error(result.message)
            }

            refreshLicenseState()
            result
        } catch (e: IllegalArgumentException) {
            Log.e(tag, "License key not found: $licenseName", e)
            val errorResult = LicenseResult.Error(e.message ?: "License key not found")
            _licenseState.value = LicenseState.Error(errorResult.message)
            errorResult
        }
    }

    suspend fun getLicenseInfo(): LicenseInfo {
        return try {
            val knoxManager = getKnoxManager()
            val activationInfo = knoxManager.licenseActivationInfo

            LicenseInfo(
                isActivated = activationInfo?.state == ActivationInfo.State.ACTIVE,
                licenseKey = null, // Not available in ActivationInfo
                activationDate = activationInfo?.activationDate?.toString(),
                expirationDate = null, // Not available in ActivationInfo
                errorCode = null,
                errorMessage = null
            )
        } catch (e: Exception) {
            Log.e(tag, "Error getting license info", e)
            LicenseInfo(
                isActivated = false,
                errorMessage = e.message ?: "Failed to get license info"
            )
        }
    }

    private suspend fun refreshLicenseState() {
        Log.d(tag, "Refreshing license state")
        try {
            val licenseInfo = getLicenseInfo()
            _licenseState.value = when {
                licenseInfo.isActivated -> LicenseState.Activated("License is active")
                licenseInfo.errorMessage != null -> LicenseState.Error(licenseInfo.errorMessage)
                else -> LicenseState.Deactivated("License is not active")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error refreshing license state", e)
            _licenseState.value = LicenseState.Error("Failed to check license state: ${e.message}")
        }
    }

    private suspend fun activateKnoxLicense(licenseKey: String): LicenseResult =
        suspendCancellableCoroutine { continuation ->
            val callback = LicenseResultCallback { licenseResult ->
                Log.d(tag, "Knox activation callback received")
                Log.d(tag, "Knox activation success: ${licenseResult.isSuccess}")
                Log.d(tag, "Knox activation error code: ${licenseResult.errorCode}")
                Log.d(tag, "Knox activation result object: $licenseResult")

                val result = when (licenseResult.isSuccess) {
                    true -> {
                        Log.i(tag, "Knox activation successful")
                        LicenseResult.Success("License activated successfully")
                    }
                    false -> {
                        val errorMessage = knoxErrorMapper.getKnoxErrorMessage(licenseResult.errorCode)
                        Log.e(tag, "Knox activation failed:")
                        Log.e(tag, "  Error code: ${licenseResult.errorCode}")
                        Log.e(tag, "  Error message: $errorMessage")
                        Log.e(tag, "  License key used: ${licenseKey.take(10)}...")

                        // Check for specific error codes that might indicate configuration issues
                        when (licenseResult.errorCode) {
                            KnoxEnterpriseLicenseManager.ERROR_INVALID_LICENSE -> {
                                Log.e(tag, "  This indicates the license key is invalid or malformed")
                            }
                            KnoxEnterpriseLicenseManager.ERROR_INVALID_PACKAGE_NAME -> {
                                Log.e(tag, "  This indicates the license is not valid for this package name")
                            }
                            KnoxEnterpriseLicenseManager.ERROR_NETWORK_DISCONNECTED -> {
                                Log.e(tag, "  This indicates network connectivity issues")
                            }
                            KnoxEnterpriseLicenseManager.ERROR_INTERNAL -> {
                                Log.e(tag, "  KNOX ERROR 301: This typically indicates missing Device Administrator privileges")
                                Log.e(tag, "  Your application must be registered as a Device Administrator, Device Owner, or Profile Owner")
                                Log.e(tag, "  Check if your app has Device Administrator privileges enabled in Settings")
                                Log.e(tag, "  See knox-licensing README troubleshooting section for implementation guidance")
                                checkDeviceAdministratorStatus()
                            }
                        }

                        LicenseResult.Error("Knox error code ${licenseResult.errorCode}: $errorMessage", licenseResult.errorCode)
                    }
                }
                continuation.resume(result)
            }

            try {
                val knoxManager = getKnoxManager()
                Log.d(tag, "Calling Knox activateLicense with key: ${licenseKey.take(10)}...")
                Log.d(tag, "Knox manager instance: $knoxManager")
                Log.d(tag, "Application package name: ${context.packageName}")
                Log.d(tag, "Knox manager class: ${knoxManager.javaClass.name}")

                // Check if Knox is properly initialized
                try {
                    val activationInfo = knoxManager.licenseActivationInfo
                    Log.d(tag, "Current license activation info: $activationInfo")
                    Log.d(tag, "Current license state: ${activationInfo?.state}")
                } catch (e: Exception) {
                    Log.w(tag, "Could not get current license activation info", e)
                }

                knoxManager.activateLicense(licenseKey, callback)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(tag, "Exception during Knox activation call", e)
                val errorCode = when (e) {
                    is SecurityException -> KnoxEnterpriseLicenseManager.ERROR_INTERNAL
                    is IllegalArgumentException -> KnoxEnterpriseLicenseManager.ERROR_INVALID_LICENSE
                    else -> KnoxEnterpriseLicenseManager.ERROR_UNKNOWN
                }
                Log.e(tag, "Mapped exception to Knox error code: $errorCode")
                continuation.resume(
                    LicenseResult.Error(knoxErrorMapper.getKnoxErrorMessage(errorCode), errorCode)
                )
            }
        }

    private suspend fun deactivateKnoxLicense(licenseKey: String): LicenseResult =
        suspendCancellableCoroutine { continuation ->
            val callback = LicenseResultCallback { licenseResult ->
                val result = when (licenseResult.isSuccess) {
                    true -> {
                        Log.d(tag, "Knox deactivation successful")
                        LicenseResult.Success("License deactivated successfully")
                    }
                    false -> {
                        val errorMessage = knoxErrorMapper.getKnoxErrorMessage(licenseResult.errorCode)
                        Log.d(tag, "Knox deactivation failed with error code: ${licenseResult.errorCode}")
                        LicenseResult.Error("$errorMessage", licenseResult.errorCode)
                    }
                }
                continuation.resume(result)
            }

            try {
                val knoxManager = getKnoxManager()
                Log.d(tag, "Deactivating license key: ${licenseKey.take(10)}...")
                knoxManager.deActivateLicense(licenseKey, callback)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val errorCode = when (e) {
                    is SecurityException -> KnoxEnterpriseLicenseManager.ERROR_INTERNAL
                    is IllegalArgumentException -> KnoxEnterpriseLicenseManager.ERROR_INVALID_LICENSE
                    else -> KnoxEnterpriseLicenseManager.ERROR_UNKNOWN
                }
                continuation.resume(
                    LicenseResult.Error(knoxErrorMapper.getKnoxErrorMessage(errorCode), errorCode)
                )
            }
        }

    private fun getKnoxManager(): KnoxEnterpriseLicenseManager {
        return KnoxEnterpriseLicenseManager.getInstance(context)
    }

    private fun checkDeviceAdministratorStatus() {
        try {
            val devicePolicyManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

            // Check if any component is a device admin
            val activeAdmins = devicePolicyManager.activeAdmins
            val hasActiveAdmins = !activeAdmins.isNullOrEmpty()

            Log.e(tag, "Device Administrator Status Check:")
            Log.e(tag, "  Has active device administrators: $hasActiveAdmins")

            if (hasActiveAdmins) {
                Log.e(tag, "  Active administrators: ${activeAdmins?.size}")
                activeAdmins?.forEach { admin ->
                    Log.e(tag, "    - ${admin.className} (package: ${admin.packageName})")
                    Log.e(tag, "      Is current app admin: ${admin.packageName == context.packageName}")
                }
            } else {
                Log.e(tag, "  NO DEVICE ADMINISTRATORS FOUND")
                Log.e(tag, "  Your application must register as a Device Administrator to use Knox licensing")
            }

            // Check Device Owner status
            val isDeviceOwner = devicePolicyManager.isDeviceOwnerApp(context.packageName)
            Log.e(tag, "  Is Device Owner: $isDeviceOwner")

            // Check Profile Owner status (if applicable)
            try {
                val isProfileOwner = devicePolicyManager.isProfileOwnerApp(context.packageName)
                Log.e(tag, "  Is Profile Owner: $isProfileOwner")
            } catch (e: Exception) {
                Log.d(tag, "  Profile Owner check not applicable or failed: ${e.message}")
            }

        } catch (e: Exception) {
            Log.e(tag, "Error checking Device Administrator status", e)
        }
    }
}
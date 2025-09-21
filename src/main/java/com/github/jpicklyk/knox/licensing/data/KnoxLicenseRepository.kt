package com.github.jpicklyk.knox.licensing.data

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
                val result = when (licenseResult.isSuccess) {
                    true -> {
                        Log.d(tag, "Knox activation successful")
                        LicenseResult.Success("License activated successfully")
                    }
                    false -> {
                        val errorMessage = knoxErrorMapper.getKnoxErrorMessage(licenseResult.errorCode)
                        Log.d(tag, "Knox activation failed with error code: ${licenseResult.errorCode}")
                        LicenseResult.Error("$errorMessage", licenseResult.errorCode)
                    }
                }
                continuation.resume(result)
            }

            try {
                val knoxManager = getKnoxManager()
                Log.d(tag, "Activating license key: ${licenseKey.take(10)}...")
                knoxManager.activateLicense(licenseKey, callback)
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
}
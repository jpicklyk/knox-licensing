package net.sfelabs.knoxmoduleshowcase.features.home

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import com.github.jpicklyk.knox.licensing.domain.KnoxLicenseHandler
import com.github.jpicklyk.knox.licensing.domain.LicenseResult
import com.github.jpicklyk.knox.licensing.domain.LicenseInfo
import com.github.jpicklyk.knox.licensing.domain.LicenseState
import javax.inject.Inject

data class PermissionStatus(val name: String, val isEnabled: Boolean)
data class KnoxLicenseStatus(
    val maskedKey: String? = null,
    val state: LicenseState = LicenseState.Loading,
    val activationDate: String? = null,
    val error: String? = null
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val knoxLicenseHandler: KnoxLicenseHandler
): ViewModel(){

    private val _deviceBuildNumber = mutableStateOf(Build.DISPLAY.split(".").last())
    val deviceBuildNumber: State<String> = _deviceBuildNumber

    private val _deviceSplValue = mutableStateOf(Build.VERSION.SECURITY_PATCH)
    val splValue: State<String> = _deviceSplValue

    private val _permissionList = mutableStateListOf<PermissionStatus>()
    val permissionState: SnapshotStateList<PermissionStatus> = _permissionList

    private val _knoxState = mutableStateOf(KnoxLicenseStatus())
    val knoxActivationState: State<KnoxLicenseStatus> = _knoxState

    init {
        viewModelScope.launch {
            try {
                // Use knox-licensing module with automatic TE3 detection
                var licenseInfo = knoxLicenseHandler.getLicenseInfo()
                var licenseState = toLicenseState(licenseInfo)

                if (!licenseInfo.isActivated) {
                    Log.d("HomeScreenViewModel", "License not activated, attempting activation with automatic license selection...")
                    val activationResult = knoxLicenseHandler.activate()
                    licenseState = toLicenseState(activationResult)
                    // Refresh license info after activation
                    licenseInfo = knoxLicenseHandler.getLicenseInfo()
                    Log.d("HomeScreenViewModel", "activationResult: $activationResult")
                }

                _permissionList.addAll(getPermissionStatus(applicationContext))
                updateKnoxActivationInfo(licenseInfo, licenseState)
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "Error during license initialization", e)
                val errorState = LicenseState.Error("License initialization failed: ${e.message}")
                updateKnoxActivationInfo(null, errorState)
            }
        }
    }

    private fun toLicenseState(licenseInfo: LicenseInfo): LicenseState {
        return if (licenseInfo.isActivated) {
            LicenseState.Activated("License is active")
        } else {
            LicenseState.Deactivated("License not activated")
        }
    }

    private fun toLicenseState(result: LicenseResult): LicenseState {
        return when (result) {
            is LicenseResult.Success -> LicenseState.Activated(result.message)
            is LicenseResult.Error -> LicenseState.Error(result.message)
        }
    }

    private fun updateKnoxActivationInfo(licenseInfo: LicenseInfo?, licenseState: LicenseState) {
        // Get the configured license key for display
        val configuredKey = knoxLicenseHandler.getAvailableLicenses()["default"]
        val maskedConfiguredKey = configuredKey?.let { maskLicenseKey(it) }

        // Use license key from LicenseInfo if available, otherwise use configured key
        val maskedKey = licenseInfo?.licenseKey?.let { maskLicenseKey(it) } ?: maskedConfiguredKey

        val error = when (licenseState) {
            is LicenseState.Error -> licenseState.message
            else -> null
        }

        _knoxState.value = _knoxState.value.copy(
            maskedKey = maskedKey,
            state = licenseState,
            activationDate = licenseInfo?.activationDate,
            error = error
        )
    }

    private fun maskLicenseKey(key: String): String {
        // Show first 10 chars then mask the rest (e.g., "KLM09-XXXX...XXX")
        return if (key.length > 10) {
            "${key.take(10)}...${key.takeLast(3)}"
        } else {
            key
        }
    }
    private fun getPermissionStatus(context: Context): List<PermissionStatus> {
        val manifestPermissionStatusList = ArrayList<PermissionStatus>()

        try {
            val packageName = context.packageName
            val packageManager = context.packageManager
            val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val requestedPermissions = packageInfo.requestedPermissions

            if (requestedPermissions != null) {
                for (permissionName in requestedPermissions) {
                    val isEnabled = packageManager.checkPermission(permissionName, packageName) == PackageManager.PERMISSION_GRANTED
                    manifestPermissionStatusList.add(PermissionStatus(
                        permissionName.split(".").last(), isEnabled)
                    )
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return manifestPermissionStatusList
    }
}




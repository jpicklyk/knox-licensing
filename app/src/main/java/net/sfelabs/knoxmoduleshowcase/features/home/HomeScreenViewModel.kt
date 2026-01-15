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
import net.sfelabs.knox.core.domain.usecase.model.ApiResult
import com.github.jpicklyk.knox.licensing.domain.KnoxLicenseHandler
import com.github.jpicklyk.knox.licensing.domain.LicenseResult
import com.github.jpicklyk.knox.licensing.domain.LicenseInfo
import net.sfelabs.knox_enterprise.license.domain.usecase.GetLicenseActivationInfoUseCase
import net.sfelabs.knox_enterprise.license.presentation.LicenseState
import java.util.Date
import javax.inject.Inject

data class PermissionStatus(val name: String, val isEnabled: Boolean)
data class KnoxLicenseStatus(
    val maskedKey: String? = null,
    val state: LicenseState = LicenseState.Loading,
    val activationDate: Date? = null,
    val error: String? = null
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val knoxLicenseHandler: KnoxLicenseHandler
): ViewModel(){

    private val getLicenseActivationInfoUseCase = GetLicenseActivationInfoUseCase()

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
                // Use new knox-licensing module with automatic TE3 detection
                val licenseInfo = knoxLicenseHandler.getLicenseInfo()
                var licenseState = convertToLegacyLicenseState(licenseInfo)

                if (!licenseInfo.isActivated) {
                    Log.d("HomeScreenViewModel", "License not activated, attempting activation with automatic license selection...")
                    val activationResult = knoxLicenseHandler.activate()
                    licenseState = convertToLegacyLicenseState(activationResult, licenseInfo)
                    Log.d("HomeScreenViewModel", "activationResult: $activationResult")
                }

                _permissionList.addAll(getPermissionStatus(applicationContext))
                updateKnoxActivationInfo(licenseState)
            } catch (e: Exception) {
                Log.e("HomeScreenViewModel", "Error during license initialization", e)
                val errorState = LicenseState.Error("License initialization failed: ${e.message}")
                updateKnoxActivationInfo(errorState)
            }
        }
    }

    // Bridge function to convert new license types to legacy format for UI compatibility
    private fun convertToLegacyLicenseState(licenseInfo: LicenseInfo): LicenseState {
        return if (licenseInfo.isActivated) {
            LicenseState.Activated("License is active")
        } else {
            LicenseState.NotActivated
        }
    }

    private fun convertToLegacyLicenseState(result: LicenseResult, @Suppress("UNUSED_PARAMETER") licenseInfo: LicenseInfo): LicenseState {
        return when (result) {
            is LicenseResult.Success -> LicenseState.Activated(result.message)
            is LicenseResult.Error -> LicenseState.Error(result.message)
        }
    }

    private suspend fun updateKnoxActivationInfo(licenseState: LicenseState) {
        // Always get the configured license key for display, even if activation fails
        val configuredKey = knoxLicenseHandler.getAvailableLicenses()["default"]
        val maskedConfiguredKey = configuredKey?.let { maskLicenseKey(it) }

        val result = getLicenseActivationInfoUseCase()
        if(result is ApiResult.Success) {
            val info = result.data
            _knoxState.value = _knoxState.value.copy(
                maskedKey = info.maskedLicenseKey ?: maskedConfiguredKey,
                state = licenseState,
                activationDate = info.activationDate
            )
        } else {
            _knoxState.value = _knoxState.value.copy(
                maskedKey = maskedConfiguredKey,
                state = licenseState,
                activationDate = null,
                error = licenseState.getErrorOrNull()
            )
        }
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




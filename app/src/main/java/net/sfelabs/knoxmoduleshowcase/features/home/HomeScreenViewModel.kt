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
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.knox_enterprise.license.domain.usecase.GetLicenseActivationInfoUseCase
import net.sfelabs.knox_enterprise.license.domain.usecase.GetLicenseInfoUseCase
import net.sfelabs.knox_enterprise.license.domain.usecase.KnoxLicenseUseCase
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
    private val knoxLicenseUseCase: KnoxLicenseUseCase,
    private val getLicenseInfoUseCase: GetLicenseInfoUseCase
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
            var licenseState = getLicenseInfoUseCase()
            if(licenseState.isNotActivated()) {
                licenseState = knoxLicenseUseCase(activate = true)
                Log.d("HomeScreenViewModel", "activationResult: $licenseState")
            }

            _permissionList.addAll(getPermissionStatus(applicationContext))
            updateKnoxActivationInfo(licenseState)
        }
    }

    private suspend fun updateKnoxActivationInfo(licenseState: LicenseState) {

        val result = getLicenseActivationInfoUseCase()
        if(result is ApiResult.Success) {
            val info = result.data
            _knoxState.value = _knoxState.value.copy(
                maskedKey = info.maskedLicenseKey,
                state = licenseState,
                activationDate = info.activationDate
            )
        } else {
            _knoxState.value = _knoxState.value.copy(
                maskedKey = null,
                state = licenseState,
                activationDate = null,
                error = licenseState.getErrorOrNull()
            )
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




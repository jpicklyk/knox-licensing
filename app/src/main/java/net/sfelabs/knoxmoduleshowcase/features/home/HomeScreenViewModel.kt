package net.sfelabs.knoxmoduleshowcase.features.home

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samsung.android.knox.license.ActivationInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_common.domain.use_cases.license.GetLicenseActivationInfoUseCase
import java.util.Date
import javax.inject.Inject

data class PermissionStatus(val name: String, val isEnabled: Boolean)
data class KnoxLicenseStatus(
    val maskedKey: String? = null,
    val state: ActivationInfo.State? = null,
    val activationDate: Date? = null
)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val getLicenseActivationInfoUseCase: GetLicenseActivationInfoUseCase
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
            _permissionList.addAll(getPermissionStatus(applicationContext))
            updateKnoxActivationInfo()
        }
    }

    private suspend fun updateKnoxActivationInfo() {
        val results = getLicenseActivationInfoUseCase()
        if(results is ApiResult.Success) {
            val info = results.data
            _knoxState.value = _knoxState.value.copy(
                maskedKey = info.maskedLicenseKey,
                state = info.state,
                activationDate = info.activationDate
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
        manifestPermissionStatusList.removeLast()
        return manifestPermissionStatusList
    }
}




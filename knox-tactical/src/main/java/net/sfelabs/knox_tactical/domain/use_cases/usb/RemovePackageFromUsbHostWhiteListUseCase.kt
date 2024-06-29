package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.AppIdentity
import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.application.ApplicationPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class RemovePackageFromUsbHostWhiteListUseCase @Inject constructor(
    @TacticalSdk private val enterpriseDeviceManager: EnterpriseDeviceManager
) {
    suspend operator fun invoke(appIdentity: AppIdentity): UnitApiCall {
        return coroutineScope {
            try {
                val appPolicy: ApplicationPolicy = enterpriseDeviceManager.applicationPolicy
                val result = appPolicy.removePackageFromUsbHostWhiteList(appIdentity)
                if (result != ApplicationPolicy.ERROR_NONE) {
                    ApiResult.Error(
                        UiText.DynamicString(
                            "removePackageToUsbHostWhiteList error: $result"
                        ))
                } else {
                    ApiResult.Success(Unit)
                }
            } catch (e: Exception) {
                ApiResult.Error(
                    UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}
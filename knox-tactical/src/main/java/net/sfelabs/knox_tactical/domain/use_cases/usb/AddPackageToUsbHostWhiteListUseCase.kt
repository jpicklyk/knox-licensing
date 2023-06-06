package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.AppIdentity
import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.application.ApplicationPolicy
import com.samsung.android.knox.custom.CustomDeviceManager

import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.common.core.UnitApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class AddPackageToUsbHostWhiteListUseCase @Inject constructor(
    @TacticalSdk private val enterpriseDeviceManager: EnterpriseDeviceManager
) {
    suspend operator fun invoke(appIdentity: AppIdentity): UnitApiCall {
        return coroutineScope {
            try {
                val appPolicy: ApplicationPolicy = enterpriseDeviceManager.applicationPolicy
                val result = appPolicy.addPackageToUsbHostWhiteList(appIdentity)
                if (result != CustomDeviceManager.SUCCESS) {
                    ApiCall.Error(
                        UiText.DynamicString(
                            "addPackageToUsbHostWhiteList error: $result"
                        ))
                } else {
                    ApiCall.Success(Unit)
                }
            } catch (e: Exception) {
                ApiCall.Error(
                    UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}
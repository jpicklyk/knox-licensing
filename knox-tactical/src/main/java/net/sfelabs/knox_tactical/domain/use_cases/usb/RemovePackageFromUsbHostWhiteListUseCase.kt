package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.AppIdentity
import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.application.ApplicationPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class RemovePackageFromUsbHostWhiteListUseCase @Inject constructor(
    @TacticalSdk private val enterpriseDeviceManager: EnterpriseDeviceManager
) {
    suspend operator fun invoke(appIdentity: AppIdentity): net.sfelabs.core.ui.UnitApiCall {
        return coroutineScope {
            try {
                val appPolicy: ApplicationPolicy = enterpriseDeviceManager.applicationPolicy
                val result = appPolicy.removePackageFromUsbHostWhiteList(appIdentity)
                if (result != ApplicationPolicy.ERROR_NONE) {
                    net.sfelabs.core.ui.ApiCall.Error(
                        net.sfelabs.core.ui.UiText.DynamicString(
                            "removePackageToUsbHostWhiteList error: $result"
                        ))
                } else {
                    net.sfelabs.core.ui.ApiCall.Success(Unit)
                }
            } catch (e: Exception) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}
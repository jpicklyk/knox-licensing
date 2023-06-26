package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.AppIdentity
import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.application.ApplicationPolicy
import com.samsung.android.knox.custom.CustomDeviceManager

import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class AddPackageToUsbHostWhiteListUseCase @Inject constructor(
    @TacticalSdk private val enterpriseDeviceManager: EnterpriseDeviceManager
) {
    suspend operator fun invoke(enable: Boolean, appIdentity: AppIdentity): net.sfelabs.core.ui.UnitApiCall {
        return coroutineScope {
            try {
                val restrictionPolicy = enterpriseDeviceManager.restrictionPolicy
                restrictionPolicy.allowUsbHostStorage(!enable)
                val appPolicy: ApplicationPolicy = enterpriseDeviceManager.applicationPolicy
                val result =
                    if(enable) {
                        appPolicy.addPackageToUsbHostWhiteList(appIdentity)
                    } else {
                        appPolicy.removePackageFromUsbHostWhiteList(appIdentity)
                    }
                if (result != CustomDeviceManager.SUCCESS) {
                    net.sfelabs.core.ui.ApiCall.Error(
                        net.sfelabs.core.ui.UiText.DynamicString(
                            "addPackageToUsbHostWhiteList error: $result"
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
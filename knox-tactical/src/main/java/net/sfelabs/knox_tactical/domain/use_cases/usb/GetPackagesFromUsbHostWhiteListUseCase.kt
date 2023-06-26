package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.application.ApplicationPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetPackagesFromUsbHostWhiteListUseCase @Inject constructor(
    @TacticalSdk private val enterpriseDeviceManager: EnterpriseDeviceManager
) {
    suspend operator fun invoke(): net.sfelabs.core.ui.ApiCall<List<String>> {
        return coroutineScope {
            try {
                val appPolicy: ApplicationPolicy = enterpriseDeviceManager.applicationPolicy
                net.sfelabs.core.ui.ApiCall.Success(appPolicy.packagesFromUsbHostWhiteList)

            } catch (e: Exception) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}
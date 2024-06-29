package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.application.ApplicationPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetPackagesFromUsbHostWhiteListUseCase @Inject constructor(
    @TacticalSdk private val enterpriseDeviceManager: EnterpriseDeviceManager
) {
    suspend operator fun invoke(): ApiResult<List<String>> {
        return coroutineScope {
            try {
                val appPolicy: ApplicationPolicy = enterpriseDeviceManager.applicationPolicy
                ApiResult.Success(appPolicy.packagesFromUsbHostWhiteList)

            } catch (e: Exception) {
                ApiResult.Error(
                    UiText.DynamicString(
                        e.message!!
                    ))
            }
        }
    }
}
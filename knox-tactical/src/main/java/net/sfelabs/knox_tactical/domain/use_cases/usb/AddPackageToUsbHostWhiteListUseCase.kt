package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.AppIdentity
import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.application.ApplicationPolicy
import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError

class AddPackageToUsbHostWhiteListUseCase: KnoxContextAwareUseCase<AddPackageToUsbHostWhiteListUseCase.Params, Unit>() {
    class Params(val enable: Boolean, val appIdentity: AppIdentity)

    private val restrictionPolicy =
        EnterpriseDeviceManager.getInstance(knoxContext).restrictionPolicy
    private val appPolicy: ApplicationPolicy =
        EnterpriseDeviceManager.getInstance(knoxContext).applicationPolicy

    suspend operator fun invoke(enable: Boolean, appIdentity: AppIdentity): UnitApiCall {
        return invoke(Params(enable, appIdentity))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        restrictionPolicy.allowUsbHostStorage(!params.enable)

        val result =
            if(params.enable) {
                appPolicy.addPackageToUsbHostWhiteList(params.appIdentity)
            } else {
                appPolicy.removePackageFromUsbHostWhiteList(params.appIdentity)
            }
        return if (result != CustomDeviceManager.SUCCESS) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "addPackageToUsbHostWhiteList error: $result"
                )
            )
        } else {
            ApiResult.Success(Unit)
        }
    }
}
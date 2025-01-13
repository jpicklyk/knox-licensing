package net.sfelabs.knox_tactical.domain.use_cases.usb

import com.samsung.android.knox.AppIdentity
import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.application.ApplicationPolicy
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class RemovePackageFromUsbHostWhiteListUseCase: WithAndroidApplicationContext, SuspendingUseCase<RemovePackageFromUsbHostWhiteListUseCase.Params, Unit>() {
    class Params(val appIdentity: AppIdentity)

    private val appPolicy = EnterpriseDeviceManager.getInstance(applicationContext).applicationPolicy

    suspend operator fun invoke(appIdentity: AppIdentity): UnitApiCall {
        return invoke(Params(appIdentity))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        val result = appPolicy.removePackageFromUsbHostWhiteList(params.appIdentity)
        return if (result != ApplicationPolicy.ERROR_NONE) {
            ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "removePackageToUsbHostWhiteList error: $result"
                )
            )
        } else {
            ApiResult.Success(Unit)
        }
    }
}
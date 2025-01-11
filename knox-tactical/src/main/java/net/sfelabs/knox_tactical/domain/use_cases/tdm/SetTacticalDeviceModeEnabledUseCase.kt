package net.sfelabs.knox_tactical.domain.use_cases.tdm

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.api.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.knox.api.domain.model.DefaultApiError.UnexpectedError

class SetTacticalDeviceModeEnabledUseCase : WithAndroidApplicationContext, SuspendingUseCase<Boolean, Unit>() {
    val restrictionPolicy = EnterpriseDeviceManager.getInstance(applicationContext).restrictionPolicy
    override suspend fun execute(params: Boolean): ApiResult<Unit> {
        return when (restrictionPolicy.enableTacticalDeviceMode(params)) {
            true -> ApiResult.Success(Unit)
            false -> ApiResult.Error(UnexpectedError(
                "An unexpected error occurred calling the enableTacticalDeviceMode API")
            )
        }
    }
}
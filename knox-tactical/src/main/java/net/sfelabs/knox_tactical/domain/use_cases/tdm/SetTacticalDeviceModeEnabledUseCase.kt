package net.sfelabs.knox_tactical.domain.use_cases.tdm

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError.UnexpectedError

class SetTacticalDeviceModeEnabledUseCase : KnoxContextAwareUseCase<Boolean, Unit>() {
    val restrictionPolicy = EnterpriseDeviceManager.getInstance(knoxContext).restrictionPolicy
    override suspend fun execute(params: Boolean): ApiResult<Unit> {
        return when (restrictionPolicy.enableTacticalDeviceMode(params)) {
            true -> ApiResult.Success(Unit)
            false -> ApiResult.Error(UnexpectedError(
                "An unexpected error occurred calling the enableTacticalDeviceMode API")
            )
        }
    }
}
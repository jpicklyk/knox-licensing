package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class Set2gConnectivityEnabled: SuspendingUseCase<Set2gConnectivityEnabled.Params, Unit>() {
    class Params(val enabled: Boolean)

    private val systemManager = CustomDeviceManager.getInstance().systemManager

    suspend operator fun invoke(enabled: Boolean): UnitApiCall {
        return invoke(Params(enabled))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        return when(systemManager.set2GConnectivityState(params.enabled)) {
            CustomDeviceManager.SUCCESS -> ApiResult.Success(Unit)
            else -> ApiResult.Error(DefaultApiError.UnexpectedError("The operation failed for an unknown reason."))
        }
    }
}
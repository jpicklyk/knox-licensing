package net.sfelabs.knox_tactical.domain.use_cases.calling

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class SetAutoRecordCallEnabledUseCase: SuspendingUseCase<Boolean, Unit>() {
    val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Boolean): ApiResult<Unit> {
        return when(systemManager.setAutomaticRecordCallEnabledState(params)) {
            CustomDeviceManager.SUCCESS -> ApiResult.Success(Unit)
            else -> ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "The API setAutomaticRecordCallEnabledState($params) failed."
                )
            )
        }
    }
}
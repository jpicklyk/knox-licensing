package net.sfelabs.knox_tactical.domain.use_cases.calling

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetAutoRecordCallEnabledUseCase: CoroutineApiUseCase<Boolean, Unit>() {
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
package net.sfelabs.knox_tactical.domain.use_cases.calling

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase

class GetAutoRecordCallEnabledUseCase: CoroutineApiUseCase<Unit, Boolean>(){
    val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return ApiResult.Success(systemManager.automaticRecordCallEnabledState)
    }
}
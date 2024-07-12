package net.sfelabs.knox_tactical.domain.use_cases.screen

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.use_case.CoroutineApiUseCase
import javax.inject.Inject

class GetNightVisionModeUseCase @Inject constructor() : CoroutineApiUseCase<Unit,Boolean>() {
    private val systemManager = CustomDeviceManager.getInstance().systemManager
    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return ApiResult.Success(systemManager.nightVisionModeState)
    }

}
package net.sfelabs.knox_tactical.domain.use_cases.tdm

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase

class GetTacticalDeviceModeEnabledUseCase: WithAndroidApplicationContext, SuspendingUseCase<Unit, Boolean>() {
    val restrictionPolicy = EnterpriseDeviceManager.getInstance(applicationContext).restrictionPolicy
    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return ApiResult.Success(restrictionPolicy.isTacticalDeviceModeEnabled)
    }
}
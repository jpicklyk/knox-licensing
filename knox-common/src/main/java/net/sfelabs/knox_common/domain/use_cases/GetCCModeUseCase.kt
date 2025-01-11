package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseKnoxManager
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.api.domain.usecase.base.SuspendingUseCase

class GetCCModeUseCase: WithAndroidApplicationContext, SuspendingUseCase<Unit, Int>() {
    val restrictionPolicy =
        EnterpriseKnoxManager.getInstance(applicationContext).advancedRestrictionPolicy

    override suspend fun execute(params: Unit): ApiResult<Int> {
        return ApiResult.Success(restrictionPolicy.ccModeState)
    }
}
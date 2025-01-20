package net.sfelabs.knox_tactical.domain.use_cases.ims

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.android.WithAndroidApplicationContext

class IsImsEnabledUseCase: WithAndroidApplicationContext, SuspendingUseCase<IsImsEnabledUseCase.Params, Boolean>() {
    class Params(val feature: Int = 1, val simSlotId: Int = 0)

    private val phoneRestrictionPolicy =
        EnterpriseDeviceManager.getInstance(applicationContext).phoneRestrictionPolicy

    suspend operator fun invoke(simSlotId: Int): ApiResult<Boolean> {
        return invoke(Params(simSlotId = simSlotId))
    }

    override suspend fun execute(params: Params): ApiResult<Boolean> {
        val result = phoneRestrictionPolicy.isIMSEnabled(params.feature, params.simSlotId)
        return ApiResult.Success(result)
    }
}

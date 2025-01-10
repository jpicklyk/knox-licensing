package net.sfelabs.knox_tactical.domain.use_cases.ims

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.SuspendingUseCase
import net.sfelabs.knox_tactical.domain.model.ImsState

class IsImsEnabledUseCase: WithAndroidApplicationContext, SuspendingUseCase<ImsState, Boolean>() {


    private val phoneRestrictionPolicy =
        EnterpriseDeviceManager.getInstance(applicationContext).phoneRestrictionPolicy

    suspend operator fun invoke(feature: Int, simSlotId: Int): ApiResult<Boolean> {
        return invoke(ImsState(
            isEnabled = false, //doesn't matter what we set this to
            simSlotId = simSlotId,
            feature = feature
        ))
    }

    override suspend fun execute(params: ImsState): ApiResult<Boolean> {
        return ApiResult.Success(
            phoneRestrictionPolicy.isIMSEnabled(params.feature, params.simSlotId)
        )
    }
}

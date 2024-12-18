package net.sfelabs.knox_tactical.domain.use_cases.ims

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.knox_tactical.domain.model.ImsState

class IsImsEnabledUseCase: KnoxContextAwareUseCase<ImsState, Boolean>() {


    private val phoneRestrictionPolicy =
        EnterpriseDeviceManager.getInstance(knoxContext).phoneRestrictionPolicy

    suspend operator fun invoke(feature: Int, simSlotId: Int): ApiResult<Boolean> {
        return invoke(ImsState(
            isEnabled = false, //doesn't matter what we set this to
            simSlotId = simSlotId,
            feature = feature
        ))
    }

    override suspend fun execute(state: ImsState): ApiResult<Boolean> {
        return ApiResult.Success(
            phoneRestrictionPolicy.isIMSEnabled(state.feature, state.simSlotId)
        )
    }
}

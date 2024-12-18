package net.sfelabs.knox_tactical.domain.use_cases.ims

import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.restriction.PhoneRestrictionPolicy.ERROR_INVALID_INPUT
import com.samsung.android.knox.restriction.PhoneRestrictionPolicy.ERROR_NONE
import com.samsung.android.knox.restriction.PhoneRestrictionPolicy.ERROR_NOT_SUPPORTED
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.domain.model.ImsState

class SetImsEnabled: KnoxContextAwareUseCase<ImsState, Unit>() {
    private val phoneRestrictionPolicy =
        EnterpriseDeviceManager.getInstance(knoxContext).phoneRestrictionPolicy

    suspend operator fun invoke(feature: Int = 1, simSlotId: Int = 0, enable: Boolean): UnitApiCall {
        return invoke(ImsState(
            isEnabled = enable,
            simSlotId = simSlotId,
            feature = feature
        ))
    }

    override suspend fun execute(state: ImsState): ApiResult<Unit> {
        return when(phoneRestrictionPolicy.setIMSEnabled(
            state.feature, state.isEnabled, state.simSlotId
        )) {
            ERROR_NONE -> ApiResult.Success(Unit)
            ERROR_NOT_SUPPORTED -> ApiResult.NotSupported
            ERROR_INVALID_INPUT -> ApiResult.Error(
                DefaultApiError.UnexpectedError(
                    "Invalid input provided."
                )
            )
            else -> ApiResult.Error(DefaultApiError.UnexpectedError(
                "An unknown error was encountered."
            ))
        }
    }
}
package net.sfelabs.knox_tactical.domain.use_cases.ims

import com.samsung.android.knox.EnterpriseDeviceManager
import com.samsung.android.knox.restriction.PhoneRestrictionPolicy.ERROR_INVALID_INPUT
import com.samsung.android.knox.restriction.PhoneRestrictionPolicy.ERROR_NONE
import com.samsung.android.knox.restriction.PhoneRestrictionPolicy.ERROR_NOT_SUPPORTED
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.model.DefaultApiError
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.knox_tactical.data.dto.ImsDto

class SetImsEnabled: WithAndroidApplicationContext, SuspendingUseCase<ImsDto, Unit>() {
    private val phoneRestrictionPolicy =
        EnterpriseDeviceManager.getInstance(applicationContext).phoneRestrictionPolicy

    suspend operator fun invoke(simSlotId: Int = 0, enable: Boolean): UnitApiCall {
        return invoke(ImsDto(enable, simSlotId = simSlotId))
    }

    suspend operator fun invoke(feature: Int, simSlotId: Int = 0, enable: Boolean): UnitApiCall {
        return invoke(ImsDto(enable, simSlotId = simSlotId, feature = feature))
    }

    override suspend fun execute(params: ImsDto): ApiResult<Unit> {
        return when(phoneRestrictionPolicy.setIMSEnabled(
            params.feature, params.enabled, params.simSlotId
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
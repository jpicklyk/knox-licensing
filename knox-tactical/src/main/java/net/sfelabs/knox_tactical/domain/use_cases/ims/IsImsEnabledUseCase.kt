package net.sfelabs.knox_tactical.domain.use_cases.ims

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.knox_tactical.data.dto.ImsDto

class IsImsEnabledUseCase: WithAndroidApplicationContext, SuspendingUseCase<ImsDto, ImsDto>() {
    private val phoneRestrictionPolicy =
        EnterpriseDeviceManager.getInstance(applicationContext).phoneRestrictionPolicy

    suspend operator fun invoke(simSlotId: Int): ApiResult<ImsDto> {
        return invoke(ImsDto(simSlotId = simSlotId))
    }

    override suspend fun execute(params: ImsDto): ApiResult<ImsDto> {
        val enabled = phoneRestrictionPolicy.isIMSEnabled(params.feature, params.simSlotId)
        return ApiResult.Success(params.copy(enabled = enabled))
    }
}

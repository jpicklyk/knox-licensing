package net.sfelabs.knox_tactical.domain.use_cases.wifi

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.SuspendingUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError

class EnableRandomizedMacAddressUseCase: WithAndroidApplicationContext, SuspendingUseCase<EnableRandomizedMacAddressUseCase.Params, Unit>() {
    data class Params(val enable: Boolean)

    private val restrictionPolicy =
        EnterpriseDeviceManager.getInstance(applicationContext).restrictionPolicy

    suspend operator fun invoke(enable: Boolean): UnitApiCall {
        return invoke(Params(enable))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        val result = restrictionPolicy.enableRandomisedMacAddress(params.enable)
        return if (result)
            ApiResult.Success(Unit)
        else
            ApiResult.Error(DefaultApiError.UnexpectedError("Setting Randomized Mac Address failed"))
    }
}
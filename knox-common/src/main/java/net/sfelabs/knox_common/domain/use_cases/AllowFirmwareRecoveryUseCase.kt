package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError

class AllowFirmwareRecoveryUseCase: WithAndroidApplicationContext, CoroutineApiUseCase<AllowFirmwareRecoveryUseCase.Params, Boolean>() {
    data class Params(val enable: Boolean)

    private val restrictionPolicy =
        EnterpriseDeviceManager.getInstance(applicationContext).restrictionPolicy

    suspend operator fun invoke(enable: Boolean): ApiResult<Boolean> {
        return invoke(Params(enable))
    }

    override suspend fun execute(params: Params): ApiResult<Boolean> {
        return when (restrictionPolicy.allowFirmwareRecovery(params.enable)) {
            true -> {
                ApiResult.Success(data = params.enable)
            }

            false -> {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "Failure occurred applying API allowFirmwareRecovery(${params.enable})"
                    )
                )
            }
        }
    }
}
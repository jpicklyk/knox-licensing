package net.sfelabs.knox_tactical.domain.use_cases.hdm

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.domain.usecase.model.ApiResult
import net.sfelabs.core.domain.usecase.base.SuspendingUseCase
import net.sfelabs.core.domain.usecase.model.DefaultApiError

class SetHdmPolicyUseCase: WithAndroidApplicationContext, SuspendingUseCase<SetHdmPolicyUseCase.Params, Unit>() {
    data class Params(val policy: Int, val reboot: Boolean = false)

    suspend operator fun invoke(policy: Int, reboot: Boolean): ApiResult<Unit> {
        return invoke(Params(policy, reboot))
    }

    override suspend fun execute(params: Params): ApiResult<Unit> {
        val hdmManager =
            EnterpriseDeviceManager.getInstance(applicationContext).hypervisorDeviceManager

        return when (hdmManager.stealthHwControl(params.policy, params.reboot)) {
            true -> ApiResult.Success(Unit)
            false -> ApiResult.Error(DefaultApiError.UnexpectedError("HDM Policy failed to apply"))
        }
    }
}
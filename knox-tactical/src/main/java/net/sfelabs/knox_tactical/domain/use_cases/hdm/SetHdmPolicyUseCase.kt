package net.sfelabs.knox_tactical.domain.use_cases.hdm

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.SuspendingUseCase

class SetHdmPolicyUseCase: WithAndroidApplicationContext, SuspendingUseCase<SetHdmPolicyUseCase.Params, Boolean>() {
    data class Params(val policy: Int, val reboot: Boolean = false)

    suspend operator fun invoke(policy: Int, reboot: Boolean): ApiResult<Boolean> {
        return invoke(Params(policy, reboot))
    }

    override suspend fun execute(params: Params): ApiResult<Boolean> {
        val hdmManager =
            EnterpriseDeviceManager.getInstance(applicationContext).hypervisorDeviceManager
        return ApiResult.Success(hdmManager.stealthHwControl(params.policy, params.reboot))
    }
}
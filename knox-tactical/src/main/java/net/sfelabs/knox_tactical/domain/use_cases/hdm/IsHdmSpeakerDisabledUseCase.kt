package net.sfelabs.knox_tactical.domain.use_cases.hdm

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.domain.parseHdmPolicyBlock
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.api.domain.usecase.base.SuspendingUseCase
import java.util.UUID

class IsHdmSpeakerDisabledUseCase: WithAndroidApplicationContext, SuspendingUseCase<Unit, Boolean>() {
    private val hdmFeatureBitmask = 512

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        val hdmPolicy = parseHdmPolicyBlock(
            EnterpriseDeviceManager.getInstance(applicationContext).hypervisorDeviceManager
                .getHdmPolicy(UUID.randomUUID().toString(), "stealth")
        )
        return ApiResult.Success(hdmPolicy and hdmFeatureBitmask != 0)
    }
}

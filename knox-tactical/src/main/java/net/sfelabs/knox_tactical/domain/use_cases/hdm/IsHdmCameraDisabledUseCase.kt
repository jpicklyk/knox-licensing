package net.sfelabs.knox_tactical.domain.use_cases.hdm

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.domain.parseHdmPolicyBlock
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import java.util.UUID

class IsHdmCameraDisabledUseCase: WithAndroidApplicationContext, CoroutineApiUseCase<Unit, Boolean>() {
    private val hdmFeatureBitmask = 1

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        val hdmPolicy = parseHdmPolicyBlock(
            EnterpriseDeviceManager.getInstance(applicationContext).hypervisorDeviceManager
                .getHdmPolicy(UUID.randomUUID().toString(), "stealth")
        )
        return ApiResult.Success(hdmPolicy and hdmFeatureBitmask != 0)
    }
}

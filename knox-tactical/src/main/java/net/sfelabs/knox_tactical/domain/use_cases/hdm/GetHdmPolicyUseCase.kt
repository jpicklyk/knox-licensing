package net.sfelabs.knox_tactical.domain.use_cases.hdm

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.domain.parseHdmPolicyBlock
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.SuspendingUseCase
import java.util.UUID

class GetHdmPolicyUseCase : WithAndroidApplicationContext, SuspendingUseCase<Unit, Int>() {

    override suspend fun execute(params: Unit): ApiResult<Int> {
        val hdmManager =
            EnterpriseDeviceManager.getInstance(applicationContext).hypervisorDeviceManager
        val response = hdmManager.getHdmPolicy(UUID.randomUUID().toString(), "stealth")
        return ApiResult.Success(parseHdmPolicyBlock(response))
    }
}

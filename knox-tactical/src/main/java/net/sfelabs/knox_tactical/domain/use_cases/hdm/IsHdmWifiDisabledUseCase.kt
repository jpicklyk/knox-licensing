package net.sfelabs.knox_tactical.domain.use_cases.hdm

import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.parseHdmPolicyBlock
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import java.util.UUID

class IsHdmWifiDisabledUseCase: KnoxContextAwareUseCase<Unit, Boolean>() {
    private val hdmFeatureBitmask = 8

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        val hdmPolicy = parseHdmPolicyBlock(
            EnterpriseDeviceManager.getInstance(knoxContext).hypervisorDeviceManager
                .getHdmPolicy(UUID.randomUUID().toString(), "stealth")
        )
        return ApiResult.Success(hdmPolicy and hdmFeatureBitmask != 0)
    }
}

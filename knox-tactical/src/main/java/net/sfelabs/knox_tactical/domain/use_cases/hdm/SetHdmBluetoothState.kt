package net.sfelabs.knox_tactical.domain.use_cases.hdm

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.domain.parseHdmPolicyBlock
import net.sfelabs.core.knox.android.KnoxContextAwareUseCase
import net.sfelabs.core.knox.api.domain.ApiResult
import java.util.UUID

class SetHdmBluetoothState: KnoxContextAwareUseCase<Boolean, Boolean>() {
    private val bitmask = 16

    override suspend fun execute(params: Boolean): ApiResult<Boolean> {
        val hdmManager =
            EnterpriseDeviceManager.getInstance(knoxContext).hypervisorDeviceManager
        val currentPolicy = parseHdmPolicyBlock(
            hdmManager.getHdmPolicy(
                UUID.randomUUID().toString(),
                "stealth"
            )
        )
        val newPolicy =
            if (params) {
                currentPolicy or bitmask
            } else {
                currentPolicy and bitmask.inv()
            }
        return ApiResult.Success(hdmManager.stealthHwControl(newPolicy, false))
    }
}
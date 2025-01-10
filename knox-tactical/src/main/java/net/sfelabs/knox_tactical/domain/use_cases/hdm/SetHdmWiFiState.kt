package net.sfelabs.knox_tactical.domain.use_cases.hdm

import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.domain.parseHdmPolicyBlock
import net.sfelabs.core.knox.android.WithAndroidApplicationContext
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.SuspendingUseCase
import java.util.UUID

class SetHdmWiFiState: WithAndroidApplicationContext, SuspendingUseCase<SetHdmWiFiState.Params, Boolean>() {
    data class Params(val disabled: Boolean, val reboot: Boolean = false)

    private val bitmask = 8

    suspend operator fun invoke(disabled: Boolean, reboot: Boolean = false): ApiResult<Boolean> {
        return invoke(Params(disabled, reboot))
    }

    override suspend fun execute(params: Params): ApiResult<Boolean> {
        val hdmManager =
            EnterpriseDeviceManager.getInstance(applicationContext).hypervisorDeviceManager
        val currentPolicy = parseHdmPolicyBlock(
            hdmManager.getHdmPolicy(
                UUID.randomUUID().toString(),
                "stealth"
            )
        )
        val newPolicy =
            if (params.disabled) {
                currentPolicy or bitmask
            } else {
                currentPolicy and bitmask.inv()
            }
        return ApiResult.Success(hdmManager.stealthHwControl(newPolicy, params.reboot))
    }
}
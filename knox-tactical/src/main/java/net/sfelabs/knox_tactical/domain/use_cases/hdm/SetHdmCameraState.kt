package net.sfelabs.knox_tactical.domain.use_cases.hdm

import android.content.Context
import com.samsung.android.knox.EnterpriseDeviceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.parseHdmPolicyBlock
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import java.util.UUID
import javax.inject.Inject

class SetHdmCameraState @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val bitmask = 1
    suspend operator fun invoke(disabled: Boolean): ApiResult<Boolean> {
        return coroutineScope {
            try {
                val hdmManager =
                    EnterpriseDeviceManager.getInstance(context).hypervisorDeviceManager
                val currentPolicy = parseHdmPolicyBlock(
                    hdmManager.getHdmPolicy(
                        UUID.randomUUID().toString(),
                        "stealth"
                    )
                )
                val newPolicy =
                    if (disabled) {
                        currentPolicy or bitmask
                    } else {
                        currentPolicy and bitmask.inv()
                    }
                ApiResult.Success(hdmManager.stealthHwControl(newPolicy, false))
            } catch(e: NoSuchMethodError) {
                ApiResult.NotSupported
            } catch (e: Exception) {
                ApiResult.Error(DefaultApiError.UnexpectedError("getHdmPolicy failed: ${e.message}"))
            }
        }
    }
}
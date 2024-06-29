package net.sfelabs.knox_tactical.domain.use_cases.hdm

import android.content.Context
import com.samsung.android.knox.EnterpriseDeviceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.parseHdmPolicyBlock
import java.util.UUID
import javax.inject.Inject

class SetHdmExternalMemoryState @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val bitmask = 2
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
                ApiResult.Error(UiText.DynamicString("getHdmPolicy failed: ${e.message}"))
            }
        }
    }
}
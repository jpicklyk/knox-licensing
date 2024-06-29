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

class IsHdmBluetoothDisabledUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val hdmFeatureBitmask = 16
    suspend operator fun invoke(): ApiResult<Boolean> {
        return coroutineScope {
            try {
                val hdmPolicy = parseHdmPolicyBlock(
                    EnterpriseDeviceManager.getInstance(context).hypervisorDeviceManager
                        .getHdmPolicy(UUID.randomUUID().toString(), "stealth")
                )
                ApiResult.Success(hdmPolicy and hdmFeatureBitmask != 0)
            } catch (e: NoSuchMethodError) {
                ApiResult.NotSupported
            } catch (e: Exception) {
                ApiResult.Error(UiText.DynamicString("getHdmPolicy failed: ${e.message}"))
            }
        }
    }
}

package net.sfelabs.knox_tactical.domain.use_cases.hdm

import android.content.Context
import com.samsung.android.knox.EnterpriseDeviceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.parseHdmPolicyBlock
import java.util.UUID
import javax.inject.Inject

class IsHdmWifiDisabledUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val hdmFeatureBitmask = 8
    suspend operator fun invoke(): ApiCall<Boolean> {
        return coroutineScope {
            try {
                val hdmPolicy = parseHdmPolicyBlock(
                    EnterpriseDeviceManager.getInstance(context).hypervisorDeviceManager
                        .getHdmPolicy(UUID.randomUUID().toString(), "stealth")
                )
                ApiCall.Success(hdmPolicy and hdmFeatureBitmask != 0)
            } catch (e: NoSuchMethodError) {
                ApiCall.NotSupported
            } catch (e: Exception) {
                ApiCall.Error(UiText.DynamicString("getHdmPolicy failed: ${e.message}"))
            }
        }
    }
}

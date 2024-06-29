package net.sfelabs.knox_tactical.domain.use_cases.hdm

import android.content.Context
import com.samsung.android.knox.EnterpriseDeviceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import javax.inject.Inject

class SetHdmPolicyUseCase  @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend operator fun invoke(policy: Int, reboot: Boolean): ApiResult<Boolean> {
        return coroutineScope {
            try {
                val hdmManager =
                    EnterpriseDeviceManager.getInstance(context).hypervisorDeviceManager
                ApiResult.Success(hdmManager.stealthHwControl(policy, reboot))
            } catch(e: NoSuchMethodError) {
                ApiResult.NotSupported
            } catch (e: Exception) {
                ApiResult.Error(UiText.DynamicString("getHdmPolicy failed: ${e.message}"))
            }
        }
    }
}
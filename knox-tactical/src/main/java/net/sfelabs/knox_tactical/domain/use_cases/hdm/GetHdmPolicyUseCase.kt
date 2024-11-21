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

class GetHdmPolicyUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend operator fun invoke(): ApiResult<Int> {
        return coroutineScope {
            try {
                val hdmManager =
                    EnterpriseDeviceManager.getInstance(context).hypervisorDeviceManager
                val response = hdmManager.getHdmPolicy(UUID.randomUUID().toString(), "stealth")
                ApiResult.Success(parseHdmPolicyBlock(response))
            } catch (e: NoSuchMethodError) {
                ApiResult.NotSupported
            } catch (e: Exception) {
                ApiResult.Error(DefaultApiError.UnexpectedError("getHdmPolicy failed: ${e.message}"))
            }
        }
    }
}

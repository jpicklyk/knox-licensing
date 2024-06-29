package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import javax.inject.Inject

class AllowFirmwareRecoveryUseCase @Inject constructor(
    private val enterpriseDeviceManager: EnterpriseDeviceManager
) {

    suspend operator fun invoke(enable: Boolean): ApiResult<Boolean> {
        val restrictionPolicy = enterpriseDeviceManager.restrictionPolicy
        return coroutineScope {
            try {
                when (restrictionPolicy.allowFirmwareRecovery(enable)) {
                    true -> {
                        ApiResult.Success(data = enable)
                    }

                    false -> {
                        ApiResult.Error(UiText.DynamicString("Failure occurred applying API allowFirmwareRecovery(${enable})"))
                    }
                }
            } catch (se: SecurityException) {
                ApiResult.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" permission"
                    )
                )
            }
        }
    }
}
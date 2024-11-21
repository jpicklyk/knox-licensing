package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseDeviceManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
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
                        ApiResult.Error(
                            DefaultApiError.UnexpectedError(
                                "Failure occurred applying API allowFirmwareRecovery(${enable})"
                            )
                        )
                    }
                }
            } catch (se: SecurityException) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" permission"
                    )
                )
            }
        }
    }
}
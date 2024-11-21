package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseKnoxManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import javax.inject.Inject

class SetCCModeUseCase @Inject constructor(
    private val enterpriseKnoxManager: EnterpriseKnoxManager
) {

    suspend operator fun invoke(enable: Boolean): ApiResult<Boolean> {
        val restrictionPolicy = enterpriseKnoxManager.advancedRestrictionPolicy
        return coroutineScope {
            try {
                when (restrictionPolicy.setCCMode(enable)) {
                    true -> {
                        ApiResult.Success(data = enable)
                    }

                    false -> {
                        ApiResult.Error(DefaultApiError.UnexpectedError("Failure occurred applying setCCMode(${enable})"))
                    }
                }
            } catch (se: SecurityException) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_ADVANCED_RESTRICTION\" permission"
                    )
                )
            }
        }
    }
}
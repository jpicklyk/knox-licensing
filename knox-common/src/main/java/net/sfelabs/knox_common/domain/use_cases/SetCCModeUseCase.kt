package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseKnoxManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import javax.inject.Inject

class SetCCModeUseCase @Inject constructor(
    private val enterpriseKnoxManager: EnterpriseKnoxManager
) {

    suspend operator fun invoke(enable: Boolean): ApiCall<Boolean> {
        val restrictionPolicy = enterpriseKnoxManager.advancedRestrictionPolicy
        return coroutineScope {
            try {
                when (val result = restrictionPolicy.setCCMode(enable)) {
                    true -> {
                        ApiCall.Success(data = enable)
                    }

                    false -> {
                        ApiCall.Error(UiText.DynamicString("Failure occurred applying setCCMode(${enable})"))
                    }
                }
            } catch (se: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_ADVANCED_RESTRICTION\" permission"
                    )
                )
            }
        }
    }
}
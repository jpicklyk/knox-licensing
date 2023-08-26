package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.EnterpriseKnoxManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import javax.inject.Inject

class GetCCModeUseCase @Inject constructor(
    private val enterpriseKnoxManager: EnterpriseKnoxManager
) {

    suspend operator fun invoke(): ApiCall<Int> {
        val restrictionPolicy = enterpriseKnoxManager.advancedRestrictionPolicy
        return coroutineScope {
            try {
                ApiCall.Success(restrictionPolicy.ccModeState)
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
package net.sfelabs.knox_tactical.domain.use_cases.ims

import com.samsung.android.knox.restriction.PhoneRestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class IsImsEnabledUseCase @Inject constructor(
    @TacticalSdk private val phoneRestrictionPolicy: PhoneRestrictionPolicy
) {

    suspend operator fun invoke(feature: Int, simSlotId: Int): ApiResult<Boolean> {
        return coroutineScope {
            try {
                ApiResult.Success(phoneRestrictionPolicy.isIMSEnabled(feature, simSlotId))
            } catch (se: SecurityException) {
                ApiResult.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_PHONE_RESTRICTION\" permission"
                    ))
            } catch (ex: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }
    }
}

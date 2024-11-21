package net.sfelabs.knox_tactical.domain.use_cases.ims

import com.samsung.android.knox.restriction.PhoneRestrictionPolicy
import com.samsung.android.knox.restriction.PhoneRestrictionPolicy.ERROR_INVALID_INPUT
import com.samsung.android.knox.restriction.PhoneRestrictionPolicy.ERROR_NONE
import com.samsung.android.knox.restriction.PhoneRestrictionPolicy.ERROR_NOT_SUPPORTED
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetImsEnabled @Inject constructor(
    @TacticalSdk private val phoneRestrictionPolicy: PhoneRestrictionPolicy
) {

    suspend operator fun invoke(feature: Int = 1, simSlotId: Int = 0, enable: Boolean): UnitApiCall {
        return coroutineScope {
            try {
                when(phoneRestrictionPolicy.setIMSEnabled(feature, enable, simSlotId)) {
                    ERROR_NONE -> ApiResult.Success(Unit)
                    ERROR_NOT_SUPPORTED -> ApiResult.NotSupported
                    ERROR_INVALID_INPUT -> ApiResult.Error(
                        DefaultApiError.UnexpectedError(
                            "Invalid input provided."
                        )
                    )
                    else -> ApiResult.Error(DefaultApiError.UnexpectedError("An unknown error was encountered."))
                }
            } catch (se: SecurityException) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_PHONE_RESTRICTION\" permission"
                    )
                )
            } catch (ex: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }
    }
}
package net.sfelabs.knox_tactical.domain.use_cases.wifi

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class EnableRandomizedMacAddressUseCase @Inject constructor(
    @TacticalSdk private val restrictionPolicy: RestrictionPolicy
) {
    suspend operator fun invoke(enable: Boolean): UnitApiCall {
        return coroutineScope {
            try {
                val result = restrictionPolicy.enableRandomisedMacAddress(enable)
                if (result)
                    ApiResult.Success(Unit)
                else
                    ApiResult.Error(DefaultApiError.UnexpectedError("Setting Randomized Mac Address failed"))
            } catch(se: SecurityException) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" " +
                                "permission."
                    )
                )
            }catch (ex: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }
    }
}
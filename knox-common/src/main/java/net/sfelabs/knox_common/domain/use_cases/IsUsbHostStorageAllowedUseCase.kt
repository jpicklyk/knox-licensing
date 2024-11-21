package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import javax.inject.Inject

class IsUsbHostStorageAllowedUseCase @Inject constructor(
    private val restrictionPolicy: RestrictionPolicy
) {
    suspend operator fun invoke(): ApiResult<Boolean> {
        return coroutineScope {
            try {
                ApiResult.Success(restrictionPolicy.isUsbHostStorageAllowed)
            } catch (se: SecurityException) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_RESTRICTION_MGMT\" " +
                                "permission which has a protection level of signature."
                    )
                )
            } catch (ex: NoSuchMethodException) {
                ApiResult.NotSupported
            }
        }
    }
}
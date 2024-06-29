package net.sfelabs.knox_common.domain.use_cases

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import javax.inject.Inject

class AllowUsbHostStorageUseCase @Inject constructor(
    private val restrictionPolicy: RestrictionPolicy
) {
    suspend operator fun invoke(allow: Boolean): UnitApiCall {
        return coroutineScope {
            try {
                val result = restrictionPolicy.allowUsbHostStorage(allow)
                if (result)
                    ApiResult.Success(Unit)
                else
                    ApiResult.Error(UiText.DynamicString("The API allowUsbHostStorage($allow) failed"))
            } catch (se: SecurityException) {
                ApiResult.Error(
                    UiText.DynamicString(
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
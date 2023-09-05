package net.sfelabs.knox_tactical.domain.use_cases.wifi

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.ApiResult
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetRandomizedMacAddressEnabledUseCase @Inject constructor(
    @TacticalSdk private val restrictionPolicy: RestrictionPolicy
) {
    suspend operator fun invoke(): ApiCall<ApiResult<Boolean>> {
        return coroutineScope {
            try {
                val result = restrictionPolicy.isRandomisedMacAddressEnabled
                ApiCall.Success(ApiResult(result, result))
            } catch (ex: NoSuchMethodException) {
                ApiCall.NotSupported
            }
        }
    }
}
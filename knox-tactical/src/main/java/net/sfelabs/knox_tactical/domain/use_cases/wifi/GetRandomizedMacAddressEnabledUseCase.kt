package net.sfelabs.knox_tactical.domain.use_cases.wifi

import com.samsung.android.knox.restriction.RestrictionPolicy
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.api.feature.FeatureState
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetRandomizedMacAddressEnabledUseCase @Inject constructor(
    @TacticalSdk private val restrictionPolicy: RestrictionPolicy
) {
    suspend operator fun invoke(): ApiResult<FeatureState<Boolean>> {
        return coroutineScope {
            try {
                val result = restrictionPolicy.isRandomisedMacAddressEnabled
                ApiResult.Success(FeatureState(result, result))
            } catch (ex: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }
    }
}
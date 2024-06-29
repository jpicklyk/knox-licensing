package net.sfelabs.knox_tactical.domain.use_cases.ramplus

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.api.feature.FeatureState
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetRamPlusDisabledStateUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(): ApiResult<FeatureState<Boolean>> {
        return coroutineScope {
            try {
                val result = systemManager.ramPlusDisableState
                ApiResult.Success(FeatureState(!result, result))
            } catch (e: SecurityException) {
                ApiResult.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SYSTEM\" permission"
                    ))
            } catch (nsm: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }
    }
}
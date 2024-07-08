package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.api.feature.FeatureState
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class Get5gBandLockingUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(): ApiResult<FeatureState<Int>> {
        return coroutineScope {
            try {
                when(val result = systemManager.get5GBandLocking()) {
                    CustomDeviceManager.BANDLOCK_NONE -> ApiResult.Success(FeatureState(false, result))
                    else -> ApiResult.Success(FeatureState(true, result))
                }
            } catch (se: SecurityException) {
                ApiResult.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                    ))
            } catch (nsm: NoSuchMethodError) {
                ApiResult.NotSupported
            } catch (ex: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }
    }
}
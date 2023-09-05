package net.sfelabs.knox_tactical.domain.use_cases.lte

import com.samsung.android.knox.custom.CustomDeviceManager.BANDLOCK_NONE
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetBandLockingStateUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(): ApiCall<ApiResult<Int>> {
        return coroutineScope {
            try {
                when(val result = systemManager.lteBandLocking) {
                    BANDLOCK_NONE -> ApiCall.Success(ApiResult(false, result))
                    else -> ApiCall.Success(ApiResult(true, result))
                }
            } catch (se: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                    "The use of this API requires the caller to have the " +
                            "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                ))
            } catch (ex: NoSuchMethodError) {
                ApiCall.NotSupported
            }
        }
    }
}

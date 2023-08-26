package net.sfelabs.knox_tactical.domain.use_cases.lte

import com.samsung.android.knox.custom.CustomDeviceManager.BANDLOCK_NONE
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.KnoxApiEnabled
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetBandLockingStateUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
): KnoxApiEnabled {

    suspend operator fun invoke(): ApiCall<Int> {
        return coroutineScope {
            try {
                val result = systemManager.lteBandLocking
                ApiCall.Success(result)
            } catch (se: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                    "The use of this API requires the caller to have the " +
                            "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                ))
            }
        }
    }

    override suspend fun isApiEnabled(): ApiCall<Boolean> {
        return when(val result = invoke()) {
            is ApiCall.Error -> result
            ApiCall.NotSupported -> ApiCall.NotSupported
            is ApiCall.Success ->  {
                if(result.data == BANDLOCK_NONE) {
                    ApiCall.Success(false)
                } else {
                    ApiCall.Success(true)
                }
            }
        }

    }
}
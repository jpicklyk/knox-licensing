package net.sfelabs.knox_tactical.domain.use_cases.lockscreen

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetLockscreenTimeoutUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(): ApiResult<Int> {
        return coroutineScope {
            try {
                ApiResult.Success(
                    data = systemManager.activityTime
                )
            } catch (nsm: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }
    }
}
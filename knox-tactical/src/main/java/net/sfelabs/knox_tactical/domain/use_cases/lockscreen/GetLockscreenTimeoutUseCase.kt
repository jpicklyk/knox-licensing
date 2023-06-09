package net.sfelabs.knox_tactical.domain.use_cases.lockscreen

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetLockscreenTimeoutUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(): ApiCall<Int> {
        return coroutineScope {
            try {
                ApiCall.Success(
                    data = systemManager.activityTime
                )
            } catch (nsm: NoSuchMethodError) {
                ApiCall.NotSupported
            }
        }
    }
}
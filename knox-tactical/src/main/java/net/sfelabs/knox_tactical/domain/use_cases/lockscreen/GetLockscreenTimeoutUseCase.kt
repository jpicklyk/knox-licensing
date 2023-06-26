package net.sfelabs.knox_tactical.domain.use_cases.lockscreen

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetLockscreenTimeoutUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(): net.sfelabs.core.ui.ApiCall<Int> {
        return coroutineScope {
            try {
                net.sfelabs.core.ui.ApiCall.Success(
                    data = systemManager.activityTime
                )
            } catch (nsm: NoSuchMethodError) {
                net.sfelabs.core.ui.ApiCall.NotSupported
            }
        }
    }
}
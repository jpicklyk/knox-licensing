package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class Is2gConnectivityEnabledUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(): ApiResult<Boolean> {
        return coroutineScope {
            try {
                ApiResult.Success(systemManager.get2GConnectivityState())
            } catch (ex: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }
    }
}
package net.sfelabs.knox_tactical.domain.use_cases.tcp

import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class IsTcpDumpEnabled @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(): ApiResult<Boolean> {
        return coroutineScope {
            try {
                ApiResult.Success(systemManager.isTcpDumpEnabled)
            } catch (se: SecurityException) {
                ApiResult.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                    ))
            }
        }
    }
}
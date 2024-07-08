package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.LteNrModeState
import javax.inject.Inject

class Get5gNrModeUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(): ApiResult<LteNrModeState> {
        return coroutineScope {
            try {
                val result = systemManager.get5gNrModeState()
                if( result == CustomDeviceManager.ERROR_FAIL ) {
                    ApiResult.Error(
                        UiText.DynamicString(
                            "Getting 5gNrModeState error: Unknown reason"
                        ))
                } else {
                    ApiResult.Success(LteNrModeState.invoke(result))
                }
            } catch (nsm: NoSuchMethodError) {
                ApiResult.NotSupported
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
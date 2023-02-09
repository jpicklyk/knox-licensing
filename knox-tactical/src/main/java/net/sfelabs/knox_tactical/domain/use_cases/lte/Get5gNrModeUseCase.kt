package net.sfelabs.knox_tactical.domain.use_cases.lte

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.UnitApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.LteNrModeState
import javax.inject.Inject

class Get5gNrModeUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(): ApiCall<LteNrModeState> {
        return coroutineScope {
            try {
                val result = systemManager.get5gNrModeState()
                if( result == CustomDeviceManager.ERROR_FAIL ) {
                    ApiCall.Error(
                        UiText.DynamicString(
                            "Getting 5gNrModeState error: Unknown reason"
                        ))
                } else {
                    ApiCall.Success(LteNrModeState.invoke(result))
                }
            } catch (se: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                    ))
            }
        }
    }
}
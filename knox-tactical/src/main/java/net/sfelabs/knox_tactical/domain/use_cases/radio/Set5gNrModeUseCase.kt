package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.LteNrModeState
import javax.inject.Inject

class Set5gNrModeUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(state: LteNrModeState, simSlotId: Int? = null): UnitApiCall {
        simSlotId?.let { slotId ->
            if (slotId !in 0..1) {
                return ApiResult.Error(UiText.DynamicString("Invalid sim slot id: $slotId"))
            }
        }
        return coroutineScope {
            try {
                val result = when (simSlotId) {
                    null -> systemManager.set5gNrModeState(state.value)
                    else -> systemManager.set5gNrModeStatePerSimSlot(state.value, simSlotId)
                }
                if( result != CustomDeviceManager.SUCCESS ) {
                    ApiResult.Error(
                        UiText.DynamicString(
                            "Setting 5gNrModeState error: $result"
                        ))
                } else {
                    ApiResult.Success(Unit)
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
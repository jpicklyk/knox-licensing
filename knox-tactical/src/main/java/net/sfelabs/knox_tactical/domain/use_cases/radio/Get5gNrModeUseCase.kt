package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.knox.api.domain.model.ApiResult
import net.sfelabs.core.knox.api.domain.model.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.domain.model.LteNrModeState
import javax.inject.Inject

class Get5gNrModeUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(simSlotId: Int? = null): ApiResult<LteNrModeState> {
        simSlotId?.let { slotId ->
            if (slotId !in 0..1) {
                return ApiResult.Error(DefaultApiError.UnexpectedError("Invalid sim slot id: $slotId"))
            }
        }
        return coroutineScope {
            try {
                val result = when (simSlotId) {
                    null -> systemManager.get5gNrModeState()
                    else -> {
                        println("Calling get5gNrModeStatePerSimSlot")
                        systemManager.get5gNrModeStatePerSimSlot(simSlotId)
                    }
                }
                if( result == CustomDeviceManager.ERROR_FAIL ) {
                    ApiResult.Error(
                        DefaultApiError.UnexpectedError(
                            "Getting 5gNrModeState error: Unknown reason"
                        )
                    )
                } else {
                    ApiResult.Success(LteNrModeState.invoke(result))
                }
            } catch (nsm: NoSuchMethodError) {
                ApiResult.NotSupported
            } catch (se: SecurityException) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                    )
                )
            }
        }
    }
}
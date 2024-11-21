package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class Disable5gBandLockingUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(simSlotId: Int? = null): UnitApiCall {
        simSlotId?.let { slotId ->
            if (slotId !in 0..1) {
                return ApiResult.Error(DefaultApiError.UnexpectedError("Invalid sim slot id: $slotId"))
            }
        }
        return coroutineScope {
            try {
                val result = when (simSlotId) {
                    null -> systemManager.disable5GBandLocking()
                    else -> systemManager.disable5GBandLockingPerSimSlot(simSlotId)
                }
                if( result != CustomDeviceManager.SUCCESS ) {
                    ApiResult.Error(
                        DefaultApiError.UnexpectedError(
                            "Disable5gBandLocking error: $result"
                        )
                    )
                } else {
                    ApiResult.Success(Unit)
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
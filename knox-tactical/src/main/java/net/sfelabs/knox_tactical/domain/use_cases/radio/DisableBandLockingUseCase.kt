package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class DisableBandLockingUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {
    suspend operator fun invoke(simSlotId: Int? = null): UnitApiCall {
        simSlotId?.let { slotId ->
            if (slotId !in 0..1) {
                return ApiResult.Error(UiText.DynamicString("Invalid sim slot id: $slotId"))
            }
        }
        return coroutineScope {
            try {
                val result = when (simSlotId) {
                    null -> systemManager.disableLteBandLocking()
                    else -> systemManager.disableLteBandLockingPerSimSlot(simSlotId)
                }
                if( result != CustomDeviceManager.SUCCESS ) {
                    ApiResult.Error(
                        UiText.DynamicString(
                        "DisableLteBandLocking error: $result"
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
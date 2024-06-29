package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class EnableBandLockingUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(lteBand: Int): UnitApiCall {
        return coroutineScope {
            try {
                val result = systemManager.enableLteBandLocking(lteBand)
                if( result != CustomDeviceManager.SUCCESS ) {
                    ApiResult.Error(
                        UiText.DynamicString(
                        "EnableLteBandLocking error: $result"
                    ))
                } else {
                    ApiResult.Success(Unit)
                }
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
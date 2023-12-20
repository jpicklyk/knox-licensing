package net.sfelabs.knox_tactical.domain.use_cases.radio

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class Enable5gBandLockingUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
) {

    suspend operator fun invoke(band: Int): UnitApiCall {
        return coroutineScope {
            try {
                val result = systemManager.enable5GBandLocking(band)
                if( result != CustomDeviceManager.SUCCESS ) {
                    ApiCall.Error(
                        UiText.DynamicString(
                            "Enable5gBandLocking error: $result"
                        ))
                } else {
                    ApiCall.Success(Unit)
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
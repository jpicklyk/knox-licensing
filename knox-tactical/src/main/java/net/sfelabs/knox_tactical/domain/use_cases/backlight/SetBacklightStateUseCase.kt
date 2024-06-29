package net.sfelabs.knox_tactical.domain.use_cases.backlight

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SystemManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class SetBacklightStateUseCase @Inject constructor(
    @TacticalSdk private val systemManager: SystemManager
){

    suspend operator fun invoke(enable: Boolean): UnitApiCall {
        return coroutineScope {
            try {
                when (val result = systemManager.setLcdBacklightState(enable)) {
                    CustomDeviceManager.SUCCESS -> {
                        ApiResult.Success(Unit)
                    }

                    else -> {
                        ApiResult.Error(
                            UiText.DynamicString(
                                "An error occurred calling the setLcdBacklightState API: $result"
                            )
                        )
                    }
                }
            }catch (e: NoSuchMethodError) {
                ApiResult.NotSupported
            } catch (e: SecurityException) {
                ApiResult.Error(
                    UiText.DynamicString(
                    "The use of this API requires the caller to have the " +
                            "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                ))
            }

        }
    }
}
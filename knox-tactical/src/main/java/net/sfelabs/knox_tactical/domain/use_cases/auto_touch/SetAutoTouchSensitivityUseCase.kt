package net.sfelabs.knox_tactical.domain.use_cases.auto_touch

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.toOnOrOff
import javax.inject.Inject

class SetAutoTouchSensitivityUseCase @Inject constructor(
    @TacticalSdk private val settingsManager: SettingsManager
){

    suspend operator fun invoke(enable: Boolean): UnitApiCall {
        return coroutineScope {
            try {
                when (settingsManager.setAutoAdjustTouchSensitivity(enable.toOnOrOff())) {
                    CustomDeviceManager.SUCCESS -> {
                        ApiResult.Success(Unit)
                    }
                    CustomDeviceManager.ERROR_FAIL -> {
                        ApiResult.Error(
                            DefaultApiError.UnexpectedError(
                                "An unknown error occurred attempting to set auto touch " +
                                        "sensitivity state: $enable"
                            )
                        )
                    }
                    else -> {
                        ApiResult.Error(
                            DefaultApiError.UnexpectedError(
                                "This device does not support the setAutoAdjustTouchSensitivity" +
                                        " API"
                            )
                        )
                    }
                }
            } catch (e: SecurityException) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                    )
                )
            } catch (nsm: NoSuchMethodError) {
                ApiResult.NotSupported
            }

        }
    }
}
package net.sfelabs.knox_tactical.domain.use_cases.auto_touch

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.common.core.ApiCall
import net.sfelabs.common.core.ui.UiText
import net.sfelabs.common.core.UnitApiCall
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
                        ApiCall.Success(Unit)
                    }
                    CustomDeviceManager.ERROR_FAIL -> {
                        ApiCall.Error(
                            UiText.DynamicString(
                            "An unknown error occurred attempting to set auto touch " +
                                    "sensitivity state: $enable"
                        ))
                    }
                    else -> {
                        ApiCall.Error(
                            UiText.DynamicString(
                            "This device does not support the setAutoAdjustTouchSensitivity" +
                                    " API"
                        ))
                    }
                }
            } catch (e: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                    "The use of this API requires the caller to have the " +
                            "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                ))
            }

        }
    }
}
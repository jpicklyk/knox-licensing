package net.sfelabs.knox_tactical.domain.use_cases.auto_touch

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.core.ui.UnitApiCall
import net.sfelabs.knox_tactical.di.TacticalSdk
import net.sfelabs.knox_tactical.toOnOrOff

import javax.inject.Inject

class SetAutoTouchSensitivityUseCase @Inject constructor(
    @TacticalSdk private val settingsManager: SettingsManager
){

    suspend operator fun invoke(enable: Boolean): net.sfelabs.core.ui.UnitApiCall {
        return coroutineScope {
            try {
                when (settingsManager.setAutoAdjustTouchSensitivity(enable.toOnOrOff())) {
                    CustomDeviceManager.SUCCESS -> {
                        net.sfelabs.core.ui.ApiCall.Success(Unit)
                    }
                    CustomDeviceManager.ERROR_FAIL -> {
                        net.sfelabs.core.ui.ApiCall.Error(
                            net.sfelabs.core.ui.UiText.DynamicString(
                            "An unknown error occurred attempting to set auto touch " +
                                    "sensitivity state: $enable"
                        ))
                    }
                    else -> {
                        net.sfelabs.core.ui.ApiCall.Error(
                            net.sfelabs.core.ui.UiText.DynamicString(
                            "This device does not support the setAutoAdjustTouchSensitivity" +
                                    " API"
                        ))
                    }
                }
            } catch (e: SecurityException) {
                net.sfelabs.core.ui.ApiCall.Error(
                    net.sfelabs.core.ui.UiText.DynamicString(
                    "The use of this API requires the caller to have the " +
                            "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                ))
            } catch (nsm: NoSuchMethodError) {
                net.sfelabs.core.ui.ApiCall.NotSupported
            }

        }
    }
}
package net.sfelabs.knox_tactical.domain.use_cases.auto_touch

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.ui.ApiCall
import net.sfelabs.core.ui.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetAutoTouchSensitivityUseCase @Inject constructor(
    @TacticalSdk private val settingsManager: SettingsManager
){

    suspend operator fun invoke(): net.sfelabs.core.ui.ApiCall<Boolean> {
        return coroutineScope {
            try {
                when(val result = settingsManager.autoAdjustTouchSensitivity) {
                    CustomDeviceManager.ON -> net.sfelabs.core.ui.ApiCall.Success(true)
                    CustomDeviceManager.OFF -> net.sfelabs.core.ui.ApiCall.Success(false)
                    else -> net.sfelabs.core.ui.ApiCall.Error(net.sfelabs.core.ui.UiText.DynamicString("Unexpected value returned: $result"))
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
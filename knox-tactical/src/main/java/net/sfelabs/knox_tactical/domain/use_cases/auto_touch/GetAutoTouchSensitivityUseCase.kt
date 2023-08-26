package net.sfelabs.knox_tactical.domain.use_cases.auto_touch

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.KnoxApiEnabled
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetAutoTouchSensitivityUseCase @Inject constructor(
    @TacticalSdk private val settingsManager: SettingsManager
): KnoxApiEnabled {

    suspend operator fun invoke(): ApiCall<Boolean> {
        return coroutineScope {
            try {
                when(val result = settingsManager.autoAdjustTouchSensitivity) {
                    CustomDeviceManager.ON -> ApiCall.Success(true)
                    CustomDeviceManager.OFF -> ApiCall.Success(false)
                    else -> ApiCall.Error(UiText.DynamicString("Unexpected value returned: $result"))
                }
            } catch (e: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                    "The use of this API requires the caller to have the " +
                            "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                ))
            } catch (nsm: NoSuchMethodError) {
                ApiCall.NotSupported
            }
        }
    }

    override suspend fun isApiEnabled(): ApiCall<Boolean> {
        return invoke()
    }
}
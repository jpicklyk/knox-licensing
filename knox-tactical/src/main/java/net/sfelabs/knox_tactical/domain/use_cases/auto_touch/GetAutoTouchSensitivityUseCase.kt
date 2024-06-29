package net.sfelabs.knox_tactical.domain.use_cases.auto_touch

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.api.feature.FeatureState
import net.sfelabs.core.domain.UiText
import net.sfelabs.knox_tactical.di.TacticalSdk
import javax.inject.Inject

class GetAutoTouchSensitivityUseCase @Inject constructor(
    @TacticalSdk private val settingsManager: SettingsManager
) {

    suspend operator fun invoke(): ApiResult<FeatureState<Int>> {
        return coroutineScope {
            try {
                when(val result = settingsManager.autoAdjustTouchSensitivity) {
                    CustomDeviceManager.ON -> ApiResult.Success(FeatureState(true, result))
                    CustomDeviceManager.OFF -> ApiResult.Success(FeatureState(false, result))
                    else -> ApiResult.Error(UiText.DynamicString("Unexpected value returned: $result"))
                }
            } catch (e: SecurityException) {
                ApiResult.Error(
                    UiText.DynamicString(
                    "The use of this API requires the caller to have the " +
                            "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                ))
            } catch (nsm: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }
    }
}
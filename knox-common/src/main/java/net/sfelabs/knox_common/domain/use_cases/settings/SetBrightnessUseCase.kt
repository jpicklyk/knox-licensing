package net.sfelabs.knox_common.domain.use_cases.settings

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import javax.inject.Inject

class SetBrightnessUseCase @Inject constructor(
    private val settingsManager: SettingsManager
) {
    suspend operator fun invoke(enable: Boolean, level: Int = 255): UnitApiCall {
        return coroutineScope {
            try {
                if (!enable) {
                    settingsManager.setBrightness(CustomDeviceManager.USE_AUTO)
                    ApiResult.Success(Unit)
                } else {
                    val result = settingsManager.setBrightness(level)
                    if (result == CustomDeviceManager.SUCCESS) {
                        ApiResult.Success(Unit)
                    } else {
                        ApiResult.Error(UiText.DynamicString("An invalid brightness level of '$level' was passed"))
                    }
                }
            } catch (e: SecurityException) {
                ApiResult.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                    )
                )
            } catch (e: NoSuchMethodError) {
                ApiResult.NotSupported
            } catch (e: Exception) {
                ApiResult.Error(
                    UiText.DynamicString(
                        e.message!!
                    )
                )
            }
        }

    }
}
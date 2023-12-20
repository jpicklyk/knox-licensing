package net.sfelabs.knox_tactical.domain.use_cases.screen

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.UiText
import net.sfelabs.core.domain.UnitApiCall
import javax.inject.Inject

class SetExtraBrightnessUseCase @Inject constructor(
    private val settingsManager: SettingsManager
){
    suspend operator fun invoke(enable: Boolean): UnitApiCall {
        return coroutineScope {
            try {
                val result = if(enable) {
                    settingsManager.setExtraBrightness(CustomDeviceManager.ON)
                } else {
                    settingsManager.setExtraBrightness(CustomDeviceManager.OFF)
                }
                when (result) {
                    CustomDeviceManager.SUCCESS -> ApiCall.Success(Unit)
                    CustomDeviceManager.ERROR_NOT_SUPPORTED -> ApiCall.NotSupported
                    else -> {
                        ApiCall.Error(UiText.DynamicString("Unknown error occurred"))
                    }
                }
            } catch (e: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                    )
                )
            } catch (e: NoSuchMethodError) {
                ApiCall.NotSupported
            } catch (e: Exception) {
                ApiCall.Error(
                    UiText.DynamicString(
                        e.message!!
                    )
                )
            }

        }
    }
}
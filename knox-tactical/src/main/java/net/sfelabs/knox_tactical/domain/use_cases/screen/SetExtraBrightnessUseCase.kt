package net.sfelabs.knox_tactical.domain.use_cases.screen

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.UnitApiCall
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
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
                    CustomDeviceManager.SUCCESS -> ApiResult.Success(Unit)
                    CustomDeviceManager.ERROR_NOT_SUPPORTED -> ApiResult.NotSupported
                    else -> {
                        ApiResult.Error(DefaultApiError.UnexpectedError("Unknown error occurred"))
                    }
                }
            } catch (e: SecurityException) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "The use of this API requires the caller to have the " +
                                "\"com.samsung.android.knox.permission.KNOX_CUSTOM_SETTING\" permission"
                    )
                )
            } catch (e: NoSuchMethodError) {
                ApiResult.NotSupported
            } catch (e: Exception) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        e.message!!
                    )
                )
            }

        }
    }
}
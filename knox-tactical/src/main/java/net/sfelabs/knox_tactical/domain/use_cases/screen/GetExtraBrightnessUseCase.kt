package net.sfelabs.knox_tactical.domain.use_cases.screen

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.ApiResult
import net.sfelabs.core.domain.UiText
import javax.inject.Inject

class GetExtraBrightnessUseCase @Inject constructor(
    private val settingsManager: SettingsManager
){
    suspend operator fun invoke(): ApiCall<ApiResult<Boolean>> {
        return coroutineScope {
            try {
                when (settingsManager.extraBrightness) {
                    CustomDeviceManager.ON ->
                        ApiCall.Success(ApiResult(enabled = true, apiValue = true))
                    CustomDeviceManager.OFF ->
                        ApiCall.Success(ApiResult(enabled = false, apiValue = false))
                    else -> {
                        ApiCall.Error(UiText.DynamicString("Unknown error occurred"))
                    }
                }
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
package net.sfelabs.knox_tactical.domain.use_cases.screen

import com.samsung.android.knox.custom.CustomDeviceManager
import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.core.knox.feature.internal.model.FeatureState
import javax.inject.Inject

class GetExtraBrightnessUseCase @Inject constructor(
    private val settingsManager: SettingsManager
){
    suspend operator fun invoke(): ApiResult<FeatureState<Boolean>> {
        return coroutineScope {
            try {
                when (settingsManager.extraBrightness) {
                    CustomDeviceManager.ON ->
                        ApiResult.Success(
                            FeatureState(
                                enabled = true,
                                value = true
                            )
                        )
                    CustomDeviceManager.OFF ->
                        ApiResult.Success(FeatureState(enabled = false, value = false))
                    else -> {
                        ApiResult.Error(DefaultApiError.UnexpectedError("Unknown error occurred"))
                    }
                }
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
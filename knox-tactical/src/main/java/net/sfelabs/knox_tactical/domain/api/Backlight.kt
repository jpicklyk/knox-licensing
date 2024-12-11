@file:Feature(
    name = "disable_backlight",
    description = "This feature switches ON or OFF the screen's backlight.  " +
            "On OLED screens, this results in the screen being completely on or off.",
    category = FeatureCategory.Toggle,
    stateMapping = StateMapping.INVERTED,
)
package net.sfelabs.knox_tactical.domain.api

import android.content.Context
import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.core.knox.feature.annotation.Feature
import net.sfelabs.core.knox.feature.annotation.FeatureGetter
import net.sfelabs.core.knox.feature.annotation.FeatureSetter
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.internal.component.StateMapping
import javax.inject.Inject

@FeatureGetter
class GetLcdBacklightEnabledUseCase @Inject constructor(context: Context) : CoroutineApiUseCase<Unit, Boolean>() {
    val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return ApiResult.Success(systemManager.lcdBacklightState)
    }
}

@FeatureSetter
class SetLcdBacklightEnabledUseCase @Inject constructor(context: Context) : CoroutineApiUseCase<Boolean, Unit>() {
    val systemManager = CustomDeviceManager.getInstance().systemManager

    override suspend fun execute(params: Boolean): ApiResult<Unit> {
        return when(val result =systemManager.setLcdBacklightState(params)) {
            CustomDeviceManager.SUCCESS -> {
                ApiResult.Success(Unit)
            }
            else -> {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "An error occurred calling the setLcdBacklightState API: $result"
                    )
                )
            }
        }
    }
}

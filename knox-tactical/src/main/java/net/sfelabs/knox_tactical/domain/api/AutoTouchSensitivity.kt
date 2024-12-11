package net.sfelabs.knox_tactical.domain.api

import com.samsung.android.knox.custom.CustomDeviceManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.core.knox.feature.annotation.FeatureDefinition
import net.sfelabs.core.knox.feature.internal.component.StateMapping
import net.sfelabs.core.knox.feature.api.FeatureCategory
import net.sfelabs.core.knox.feature.api.FeatureContract
import net.sfelabs.knox_tactical.toOnOrOff

@FeatureDefinition(
    title = "Enable Auto Sensitivity",
    description = "This feature switches ON or OFF the touch sensitivity functionality in the " +
        "device settings.",
    category = FeatureCategory.Toggle,
    stateMapping = StateMapping.DIRECT,
)
class AutoTouchSensitivityFeature() : FeatureContract<Boolean> {
    private val getUseCase = GetAutoTouchSensitivityEnabledUseCase()
    private val setUseCase = SetAutoTouchSensitivityEnabledUseCase()

    override suspend fun getState(): ApiResult<Boolean> = getUseCase(Unit)
    override suspend fun setState(value: Boolean): ApiResult<Unit> = setUseCase(value)
}

class GetAutoTouchSensitivityEnabledUseCase() : CoroutineApiUseCase<Unit, Boolean>() {
    val settingsManager = CustomDeviceManager.getInstance().settingsManager

    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        return when(val result = settingsManager.autoAdjustTouchSensitivity) {
            CustomDeviceManager.ON -> ApiResult.Success(true)
            CustomDeviceManager.OFF -> ApiResult.Success(false)
            else -> ApiResult.Error(DefaultApiError.UnexpectedError("Unexpected value returned: $result"))
        }
    }
}

class SetAutoTouchSensitivityEnabledUseCase () : CoroutineApiUseCase<Boolean, Unit>() {
    val settingsManager = CustomDeviceManager.getInstance().settingsManager

    override suspend fun execute(params: Boolean): ApiResult<Unit> {
        return when (val result =settingsManager.setAutoAdjustTouchSensitivity(params.toOnOrOff())) {
            CustomDeviceManager.SUCCESS -> {
                ApiResult.Success(Unit)
            }
            CustomDeviceManager.ERROR_FAIL -> {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "An unknown error occurred attempting to set auto touch " +
                                "sensitivity state: $result"
                    )
                )
            }
            else -> {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        "This device does not support the setAutoAdjustTouchSensitivity" +
                                " API"
                    )
                )
            }
        }
    }
}
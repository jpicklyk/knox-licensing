@file:Feature(
    name = "tactical_device_mode",
    description = "Activates tactical device mode for enhanced mission capabilities",
    category = FeatureCategory.Toggle
)
package net.sfelabs.knox_tactical.domain.api.tdm

import android.content.Context
import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.core.knox.feature.annotation.Feature
import net.sfelabs.core.knox.feature.annotation.FeatureGetter
import net.sfelabs.core.knox.feature.annotation.FeatureSetter
import net.sfelabs.core.knox.feature.domain.model.FeatureCategory

@FeatureGetter
class GetTacticalDeviceModeUseCase(
    private val context: Context
) : CoroutineApiUseCase<Unit, Boolean>() {
    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        val restrictionPolicy = EnterpriseDeviceManager.getInstance(context).restrictionPolicy
        return ApiResult.Success(restrictionPolicy.isTacticalDeviceModeEnabled)
    }
}

@FeatureSetter
class SetTacticalDeviceModeUseCase(
    private val context: Context
) : CoroutineApiUseCase<Boolean, Unit>() {

    override suspend fun execute(enable: Boolean): ApiResult<Unit> {
        val restrictionPolicy = EnterpriseDeviceManager.getInstance(context).restrictionPolicy
        val result = restrictionPolicy.enableTacticalDeviceMode(enable)
        return when (result) {
            true -> ApiResult.Success(Unit)
            false -> ApiResult.Error(DefaultApiError.UnexpectedError())
        }
    }
}
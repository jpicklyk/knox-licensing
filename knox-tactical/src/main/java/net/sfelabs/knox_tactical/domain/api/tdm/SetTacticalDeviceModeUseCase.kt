package net.sfelabs.knox_tactical.domain.api.tdm

import android.content.Context
import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.core.knox.feature.annotation.FeatureUseCase

@FeatureUseCase(
    featureName = "tactical_device_mode",
    type = FeatureUseCase.Type.SETTER,
    config = Boolean::class
)
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
package net.sfelabs.knox_tactical.domain.api.tdm

import android.content.Context
import com.samsung.android.knox.EnterpriseDeviceManager
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.CoroutineApiUseCase
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.core.knox.feature.annotation.FeatureUseCase

@FeatureUseCase(
    featureName = "tactical_device_mode",
    type = FeatureUseCase.Type.GETTER,
    config = Boolean::class
)
class GetTacticalDeviceModeUseCase(
    private val context: Context
) : CoroutineApiUseCase<Unit, Boolean>() {
    override suspend fun execute(params: Unit): ApiResult<Boolean> {
        val restrictionPolicy = EnterpriseDeviceManager.getInstance(context).restrictionPolicy
        val result = restrictionPolicy.isTacticalDeviceModeEnabled
        return when (result) {
            true -> ApiResult.Success(true)
            false -> ApiResult.Error(DefaultApiError.UnexpectedError())
        }
    }
}
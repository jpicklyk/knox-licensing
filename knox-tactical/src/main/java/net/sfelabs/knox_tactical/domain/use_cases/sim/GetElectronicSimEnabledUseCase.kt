package net.sfelabs.knox_tactical.domain.use_cases.sim

import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.api.ApiResult
import net.sfelabs.core.knoxfeature.domain.model.FeatureState
import net.sfelabs.core.domain.UiText
import javax.inject.Inject

class GetElectronicSimEnabledUseCase @Inject constructor(
    private val settingsManager: SettingsManager
) {
    suspend operator fun invoke(): ApiResult<net.sfelabs.core.knoxfeature.domain.model.FeatureState<Boolean>> {
        return coroutineScope {
            try {
                val result = settingsManager.esimEnabled
                ApiResult.Success(
                    data = net.sfelabs.core.knoxfeature.domain.model.FeatureState(result, result)
                )
            } catch (e: SecurityException) {
                ApiResult.Error(
                    UiText.DynamicString(
                        e.message ?: "Calling application does not have the required permission"
                    )
                )
            } catch (e: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }


    }

}
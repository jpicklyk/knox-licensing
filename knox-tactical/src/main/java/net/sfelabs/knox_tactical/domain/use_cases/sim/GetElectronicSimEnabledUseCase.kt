package net.sfelabs.knox_tactical.domain.use_cases.sim

import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.knox.api.domain.ApiResult
import net.sfelabs.core.knox.api.domain.DefaultApiError
import net.sfelabs.core.knox.feature.internal.model.FeatureState
import javax.inject.Inject

class GetElectronicSimEnabledUseCase @Inject constructor(
    private val settingsManager: SettingsManager
) {
    suspend operator fun invoke(): ApiResult<FeatureState<Boolean>> {
        return coroutineScope {
            try {
                val result = settingsManager.esimEnabled
                ApiResult.Success(
                    data = FeatureState(result, result)
                )
            } catch (e: SecurityException) {
                ApiResult.Error(
                    DefaultApiError.UnexpectedError(
                        e.message ?: "Calling application does not have the required permission"
                    )
                )
            } catch (e: NoSuchMethodError) {
                ApiResult.NotSupported
            }
        }


    }

}
package net.sfelabs.knox_tactical.domain.use_cases.sim

import com.samsung.android.knox.custom.SettingsManager
import kotlinx.coroutines.coroutineScope
import net.sfelabs.core.domain.ApiCall
import net.sfelabs.core.domain.ApiResult
import net.sfelabs.core.domain.UiText
import javax.inject.Inject

class GetElectronicSimEnabledUseCase @Inject constructor(
    private val settingsManager: SettingsManager
) {
    suspend operator fun invoke(): ApiCall<ApiResult<Boolean>> {
        return coroutineScope {
            try {
                val result = settingsManager.esimEnabled
                ApiCall.Success(
                    data = ApiResult(result, result)
                )
            } catch (e: SecurityException) {
                ApiCall.Error(
                    UiText.DynamicString(
                        e.message ?: "Calling application does not have the required permission"
                    )
                )
            } catch (e: NoSuchMethodError) {
                ApiCall.NotSupported
            }
        }


    }

}